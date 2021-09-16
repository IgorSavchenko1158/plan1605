package com.example.plan1605.service.event;

import com.example.plan1605.model.event.request.CreateEventRequest;
import com.example.plan1605.model.event.request.MergeEventRequest;
import com.example.plan1605.model.event.response.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventOperations {
    Page<EventResponse> findFutureByEmail(String email, Pageable pageable);

    Page<EventResponse> findPastByEmail(String email, Pageable pageable);

    EventResponse createByEmail(String email, CreateEventRequest request);

    EventResponse mergeById(String email, Long id, MergeEventRequest request);

    void deleteById(String email, Long id);
}
