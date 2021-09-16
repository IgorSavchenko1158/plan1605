package com.example.plan1605.controller.event;

import com.example.plan1605.Routes;
import com.example.plan1605.model.event.request.CreateEventRequest;
import com.example.plan1605.model.event.request.MergeEventRequest;
import com.example.plan1605.model.event.response.EventResponse;
import com.example.plan1605.service.event.EventOperations;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(Routes.EVENTS)
public class EventController {

    EventOperations eventOperations;

    public EventController(EventOperations eventOperations) {
        this.eventOperations = eventOperations;
    }

    @GetMapping
    @PageableAsQueryParam
    public Page<EventResponse> getEvents(@AuthenticationPrincipal String email,
                                         @RequestParam(name = "past-events", defaultValue = "false") boolean pastEvents,
                                         @Parameter(hidden = true) Pageable pageable) {
        return pastEvents
                ? eventOperations.findPastByEmail(email, pageable)
                : eventOperations.findFutureByEmail(email, pageable);
    }

    @PostMapping
    public EventResponse createEvent(@AuthenticationPrincipal String email,
                                     @RequestBody @Valid CreateEventRequest request) {
        return eventOperations.createByEmail(email, request);
    }

    @PatchMapping("/{id}")
    public EventResponse mergeEventById(@AuthenticationPrincipal String email,
                                        @PathVariable Long id,
                                        @RequestBody @Valid MergeEventRequest request) {
        return eventOperations.mergeById(email, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@AuthenticationPrincipal String email, @PathVariable Long id) {
        eventOperations.deleteById(email, id);
    }
}
