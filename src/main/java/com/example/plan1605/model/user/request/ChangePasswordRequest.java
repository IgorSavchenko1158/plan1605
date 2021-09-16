package com.example.plan1605.model.user.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record ChangePasswordRequest(@NotNull
                                        String oldPassword,

                                        @NotBlank(message = "password must not be blank")
                                        @Size(min = 8, message = "password's length must be at least 8")
                                        String newPassword) {
}
