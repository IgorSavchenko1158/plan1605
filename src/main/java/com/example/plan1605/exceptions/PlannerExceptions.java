package com.example.plan1605.exceptions;

import com.example.plan1605.model.user.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PlannerExceptions {
    private PlannerExceptions() {
    }

    public static ResponseStatusException invalidRefreshToken(InvalidRefreshTokenException cause) {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                           "Refresh token is invalid! It may have been rotated, invalidated or expired naturally",
                                           cause);
    }

    public static ResponseStatusException duplicateEmail(String email) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email " + email + " already taken");
    }

    public static ResponseStatusException authorityNotFound(String value) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "User authority " + value + " not defined");
    }

    public static ResponseStatusException userNotFound(String email) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email " + email + " not found");
    }

    public static ResponseStatusException userNotFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
    }

    public static ResponseStatusException wrongPassword() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is incorrect");
    }

    public static ResponseStatusException eventEndsBeforeStart(Object request) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                           "Event end time is set before start time " + request);
    }

    public static ResponseStatusException eventStartsInPast(Object request) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event is set to start in the past " + request);
    }

    public static ResponseStatusException eventNotFound(UserResponse user, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                                           "Event owned by user " + user + " with id " + id + " not found");
    }
}
