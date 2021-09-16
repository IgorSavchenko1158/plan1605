package com.example.plan1605.controller.auth;

import com.example.plan1605.Routes;
import com.example.plan1605.exceptions.InvalidRefreshTokenException;
import com.example.plan1605.exceptions.PlannerExceptions;
import com.example.plan1605.model.auth.PlannerUserDetails;
import com.example.plan1605.model.auth.request.RefreshTokenRequest;
import com.example.plan1605.model.auth.request.SignInRequest;
import com.example.plan1605.model.auth.response.AccessTokenResponse;
import com.example.plan1605.service.auth.AuthOperations;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(Routes.TOKEN)
public class AuthController {

    private final AuthOperations authOperations;

    public AuthController(AuthOperations authOperations) {
        this.authOperations = authOperations;
    }

    /*
     * JWTAuthenticationFilter sets the principle (user-details from UserService) using auth manager
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = SignInRequest.class)))
    public AccessTokenResponse login(@AuthenticationPrincipal PlannerUserDetails userDetails) {
        return authOperations.getToken(userDetails);
    }

    @PostMapping(
            value = "/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public AccessTokenResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        try {
            return authOperations.refreshToken(request.refreshToken());
        } catch (InvalidRefreshTokenException e) {
            throw PlannerExceptions.invalidRefreshToken(e);
        }
    }

    @PostMapping(value = "/invalidate", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void invalidate(@RequestBody @Valid RefreshTokenRequest request, @AuthenticationPrincipal String email) {
        try {
            authOperations.invalidateToken(request.refreshToken(), email);
        } catch (InvalidRefreshTokenException e) {
            throw PlannerExceptions.invalidRefreshToken(e);
        }
    }

}