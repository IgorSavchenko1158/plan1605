package com.example.plan1605.controller.user;

import com.example.plan1605.Routes;
import com.example.plan1605.exceptions.PlannerExceptions;
import com.example.plan1605.model.user.request.ChangeEmailRequest;
import com.example.plan1605.model.user.request.ChangePasswordRequest;
import com.example.plan1605.model.user.request.ChangeUserStatusRequest;
import com.example.plan1605.model.user.request.SaveUserRequest;
import com.example.plan1605.model.user.response.UserAdminViewResponse;
import com.example.plan1605.model.user.response.UserResponse;
import com.example.plan1605.service.user.UserOperations;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(Routes.USERS)
public class UserController {

    UserOperations userOperations;

    public UserController(UserOperations userOperations) {
        this.userOperations = userOperations;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody @Valid SaveUserRequest request) {
        return userOperations.create(request);
    }

    //region any authenticated user

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal String email) {
        return userOperations.findByEmail(email).orElseThrow(() -> PlannerExceptions.userNotFound(email));
    }

    @PatchMapping("/me")
    public UserResponse changeCurrentUserEmail(@AuthenticationPrincipal String email,
                                               @RequestBody @Valid ChangeEmailRequest request) {
        return userOperations.changeEmail(email, request);
    }

    @PatchMapping("/me/password")
    public UserResponse changeCurrentUserPassword(@AuthenticationPrincipal String email,
                                                  @RequestBody @Valid ChangePasswordRequest request) {
        return userOperations.changePasswordByEmail(email, request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(@AuthenticationPrincipal String email) {
        userOperations.deleteByEmail(email);
    }

    //endregion

    //region admin only

    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerAdmin(@RequestBody @Valid SaveUserRequest request) {
        return userOperations.createAdmin(request);
    }

//    @GetMapping
//    @PageableAsQueryParam
//    public Page<UserResponse> listUsers(@Parameter(hidden = true) Pageable pageable) {
//        return userOperations.list(pageable);
//    }

    @GetMapping
    @PageableAsQueryParam
    public Page<UserAdminViewResponse> listUsers(@Parameter(hidden = true) Pageable pageable) {
        return userOperations.list(pageable);
    }

    @PatchMapping("/{id}/status")
    public UserResponse changeUserStatusById(@PathVariable long id,
                                             @RequestBody @Valid ChangeUserStatusRequest request) {
        return userOperations.changeStatusById(id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long id) {
        userOperations.deleteById(id);
    }

    //endregion
}
