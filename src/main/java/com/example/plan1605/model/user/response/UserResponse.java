package com.example.plan1605.model.user.response;

import com.example.plan1605.model.user.KnownAuthority;
import com.example.plan1605.model.user.PlannerUser;
import com.example.plan1605.model.user.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.Set;

public record UserResponse(long id,
                           String email,
                           UserStatus status,
                           @JsonFormat(shape = JsonFormat.Shape.STRING)
                           OffsetDateTime createdAt,
                           @JsonInclude(JsonInclude.Include.NON_NULL)
                           Set<KnownAuthority> authorities)  {

    public static UserResponse fromUser(PlannerUser user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt(),
                EnumSet.copyOf(user.getAuthorities().keySet()));
    }
}
