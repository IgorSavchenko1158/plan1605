package com.example.plan1605.service.user;

import com.example.plan1605.model.user.UserStatus;
import com.example.plan1605.model.user.request.ChangeEmailRequest;
import com.example.plan1605.model.user.request.ChangePasswordRequest;
import com.example.plan1605.model.user.request.SaveUserRequest;
import com.example.plan1605.model.user.response.UserAdminViewResponse;
import com.example.plan1605.model.user.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserOperations {
    UserResponse create(SaveUserRequest request);

    UserResponse createAdmin(SaveUserRequest request);

    UserResponse changeEmail(String email, ChangeEmailRequest request);

    Optional<UserResponse> findByEmail(String email);

    UserResponse changePasswordByEmail(String email, ChangePasswordRequest request);

    void deleteByEmail(String email);

    UserResponse changeStatusById(Long id, UserStatus status);

//    Page<UserResponse> list(Pageable pageable);

    Page<UserAdminViewResponse> list(Pageable pageable);

    void deleteById(Long id);
}
