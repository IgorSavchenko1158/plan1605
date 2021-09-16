package com.example.plan1605.model.auth.request;

import javax.validation.constraints.NotNull;

public record RefreshTokenRequest(@NotNull String refreshToken) {
}
