package com.example.plan1605.model.user.request;

import com.example.plan1605.model.user.UserStatus;

import javax.validation.constraints.NotNull;

public record ChangeUserStatusRequest(@NotNull UserStatus status) {
}