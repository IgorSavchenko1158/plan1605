package com.example.plan1605.model.event.response;

import com.example.plan1605.model.event.Event;
import com.example.plan1605.model.event.Recurrence;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

public record EventResponse(Long id,
                            String name,
                            @JsonFormat(shape = JsonFormat.Shape.STRING)
                            OffsetDateTime startsAt,
                            String description,
                            @JsonFormat(shape = JsonFormat.Shape.STRING)
                            OffsetDateTime endsAt,
                            Boolean bNotify,
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            Recurrence recurrence
) {

    public static EventResponse fromEvent(Event event) {
        return new EventResponse(event.getId(),
                                 event.getName(),
                                 event.getStartsAt(),
                                 event.getDescription(),
                                 event.getEndsAt(),
                                 event.isNotify(),
                                 event.getRecurrence());
    }
}
