package com.example.plan1605.service.event;

import com.example.plan1605.exceptions.PlannerExceptions;
import com.example.plan1605.model.event.Event;
import com.example.plan1605.model.event.Recurrence;
import com.example.plan1605.model.event.request.CreateEventRequest;
import com.example.plan1605.model.event.request.MergeEventRequest;
import com.example.plan1605.model.event.response.EventResponse;
import com.example.plan1605.model.user.PlannerUser;
import com.example.plan1605.model.user.response.UserResponse;
import com.example.plan1605.repository.EventRepository;
import com.example.plan1605.repository.UserRepository;
import org.quartz.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Service
public class EventService implements EventOperations {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final Scheduler scheduler;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        Scheduler scheduler) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.scheduler = scheduler;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> findFutureByEmail(String email, Pageable pageable) {
        PlannerUser user = getUser(email);
        refreshRecurringEvents(user);
        return eventRepository.findFutureByUser(user, pageable).map(EventResponse::fromEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> findPastByEmail(String email, Pageable pageable) {
        PlannerUser user = getUser(email);
        refreshRecurringEvents(user);
        return eventRepository.findPastByUser(user, pageable).map(EventResponse::fromEvent);
    }

    @Override
    @Transactional
    public EventResponse createByEmail(String email, CreateEventRequest request) {
        PlannerUser user = getUser(email);
        Event saved = save(user, request);
        schedule(saved);
        return EventResponse.fromEvent(saved);
    }

    @Override
    @Transactional
    public EventResponse mergeById(String email, Long id, MergeEventRequest request) {
        PlannerUser user = getUser(email);
        Event event = eventRepository.findByUserAndId(user, id)
                .orElseThrow(() -> PlannerExceptions.eventNotFound(UserResponse.fromUser(user), id));

        boolean notifyChange = request.bNotify() != null && (request.bNotify() != event.isNotify());
        boolean startsAtChange = request.startsAt() != null && (request.startsAt() != event.getStartsAt());
        mergeEvent(event, request);
        if (notifyChange) {
            if (event.isNotify()) {
                schedule(event);
            } else {
                unschedule(event);
            }
        } else if (startsAtChange && event.isNotify()) {
            schedule(event);
        }
        return EventResponse.fromEvent(eventRepository.save(event));
    }

    @Override
    @Transactional
    public void deleteById(String email, Long id) {
        PlannerUser user = getUser(email);
        if (!eventRepository.existsByUserAndId(user, id)) {
            throw PlannerExceptions.eventNotFound(UserResponse.fromUser(user), id);
        }
        unschedule(eventRepository.findById(id)
                           .orElseThrow(() -> PlannerExceptions.eventNotFound(UserResponse.fromUser(user), id)));

        eventRepository.deleteById(id);
    }

    @Transactional
    public void refreshRecurringEvents(PlannerUser user) {
        List<Event> events = eventRepository.findAllThatNeedRefresh(user);
        for (Event event : events) {
            Recurrence recurrence = event.getRecurrence();
            OffsetDateTime startsAt = event.getStartsAt();
            OffsetDateTime endsAt = event.getEndsAt();
            switch (recurrence) {
                case DAILY -> {
                    event.setStartsAt(startsAt.plusDays(1));
                    event.setEndsAt(endsAt.plusDays(1));
                }
                case WEEKLY -> {
                    event.setStartsAt(startsAt.plusWeeks(1));
                    event.setEndsAt(endsAt.plusWeeks(1));
                }
                case MONTHLY -> {
                    event.setStartsAt(startsAt.plusMonths(1));
                    event.setEndsAt(endsAt.plusMonths(1));
                }
                case YEARLY -> {
                    event.setStartsAt(startsAt.plusYears(1));
                    event.setEndsAt(endsAt.plusYears(1));
                }
            }
            schedule(event);
        }

    }

    private PlannerUser getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> PlannerExceptions.userNotFound(email));
    }

    private Event save(PlannerUser user, CreateEventRequest request) {
        Event event = new Event();
        event.setUser(user);
        event.setName(request.name());
        event.setStartsAt(request.startsAt());
        event.setDescription(request.description());
        event.setEndsAt(request.endsAt());
        event.setNotify(request.bNotify());
        event.setRecurrence(request.recurrence());
        return eventRepository.save(event);
    }

    private void mergeEvent(Event event, MergeEventRequest request) {
        if (request.name() != null) {
            event.setName(request.name());
        }
        if (request.startsAt() != null) {
            event.setStartsAt(request.startsAt());
        }
        if (request.endsAt() != null) {
            event.setEndsAt(request.endsAt());
        }
        if (event.getStartsAt().toLocalDate().isBefore(LocalDate.now(event.getStartsAt().getOffset()))) {
            throw PlannerExceptions.eventStartsInPast(request);
        }
        if (event.getEndsAt().isBefore(event.getStartsAt())) {
            throw PlannerExceptions.eventEndsBeforeStart(request);
        }
        if (request.description() != null) {
            event.setDescription(request.description());
        }
        if (request.bNotify() != null) {
            event.setNotify(request.bNotify());
        }
        if (request.recurrence() != null) {
            event.setRecurrence(request.recurrence());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void scanForNotifications() {
        List<Event> notifiable = eventRepository.findFutureByNotifyIsTrue();
        for (Event event : notifiable) {
            schedule(event);
        }
    }

    private JobDetail buildNotifyingJob(Event event) {
        JobDataMap map = new JobDataMap();
        map.put("event", EventResponse.fromEvent(event));
        map.put("email", event.getUser().getEmail());
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(event.getId().toString())
                .usingJobData(map)
                .build();
    }

    private Trigger buildNotifyingTrigger(JobDetail jobDetail, OffsetDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName())
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    private void schedule(Event event) {
        if (event.isNotify() != null && event.isNotify()) {
            unschedule(event);
            JobDetail jobDetail = buildNotifyingJob(event);
            Trigger trigger = buildNotifyingTrigger(jobDetail, event.getStartsAt());
            try {
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void unschedule(Event event) {
        try {
            scheduler.deleteJob(JobKey.jobKey(event.getId().toString()));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
