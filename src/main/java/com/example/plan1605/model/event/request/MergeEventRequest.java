package com.example.plan1605.model.event.request;

import com.example.plan1605.model.event.Recurrence;

import java.time.OffsetDateTime;

public record MergeEventRequest(
        String name,
        OffsetDateTime startsAt,
        String description,
        OffsetDateTime endsAt,
        Boolean bNotify,
        Recurrence recurrence
) {
}
