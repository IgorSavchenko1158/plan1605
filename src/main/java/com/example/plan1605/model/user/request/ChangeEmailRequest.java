package com.example.plan1605.model.user.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public record ChangeEmailRequest(
        @NotNull
        @Email
        String email
) {
}
