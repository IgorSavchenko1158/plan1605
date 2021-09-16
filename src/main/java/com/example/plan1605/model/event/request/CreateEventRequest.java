package com.example.plan1605.model.event.request;

import com.example.plan1605.exceptions.PlannerExceptions;
import com.example.plan1605.model.event.Recurrence;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record CreateEventRequest(
        @NotBlank
        String name,
        @NotNull
        OffsetDateTime startsAt,
        String description,
        OffsetDateTime endsAt,
        Boolean bNotify,
        Recurrence recurrence
) {
    public CreateEventRequest {
        if (startsAt.toLocalDate().isBefore(LocalDate.now(startsAt.getOffset()))) {
            throw PlannerExceptions.eventStartsInPast(this);
        }
        if (endsAt != null && endsAt.isBefore(startsAt)) {
            throw PlannerExceptions.eventEndsBeforeStart(this);
        }
    }
}
