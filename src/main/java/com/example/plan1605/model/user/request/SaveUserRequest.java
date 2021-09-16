package com.example.plan1605.model.user.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record SaveUserRequest(

        @Email(message = "email must be a valid email string")
        @NotNull(message = "email must not be null")
        String email,

        @NotBlank(message = "password must not be blank")
        @Size(min = 8, message = "password's length must be at least 8")
        String password) {

}