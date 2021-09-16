package com.example.plan1605.repository;

import com.example.plan1605.model.event.Event;
import com.example.plan1605.model.user.PlannerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e where e.user = :user and e.startsAt >= current_date")
    Page<Event> findFutureByUser(PlannerUser user, Pageable pageable);

    @Query("select e from Event e where e.user = :user and e.startsAt < current_date")
    Page<Event> findPastByUser(PlannerUser user, Pageable pageable);

    Optional<Event> findByUserAndId(PlannerUser user, Long id);

    boolean existsByUserAndId(PlannerUser user, Long id);

    int countByUser(PlannerUser user);

    int countByUserAndNotifyIsTrue(PlannerUser user);

    int countByUserAndRecurrenceIsNotNull(PlannerUser user);

    @Query("select e from Event e where e.user = ?1 and e.recurrence is not null and e.startsAt < current_date")
    List<Event> findAllThatNeedRefresh(PlannerUser user);

    @Query("select e from Event e where e.notify = true and e.startsAt > current_date")
    List<Event> findFutureByNotifyIsTrue();
}
