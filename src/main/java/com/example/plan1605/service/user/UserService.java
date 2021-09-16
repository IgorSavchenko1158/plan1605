package com.example.plan1605.service.user;

import com.example.plan1605.exceptions.PlannerExceptions;
import com.example.plan1605.model.auth.PlannerUserDetails;
import com.example.plan1605.model.user.KnownAuthority;
import com.example.plan1605.model.user.PlannerUser;
import com.example.plan1605.model.user.PlannerUserAuthority;
import com.example.plan1605.model.user.UserStatus;
import com.example.plan1605.model.user.request.ChangeEmailRequest;
import com.example.plan1605.model.user.request.ChangePasswordRequest;
import com.example.plan1605.model.user.request.SaveUserRequest;
import com.example.plan1605.model.user.response.UserAdminViewResponse;
import com.example.plan1605.model.user.response.UserResponse;
import com.example.plan1605.repository.AuthorityRepository;
import com.example.plan1605.repository.EventRepository;
import com.example.plan1605.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetailsService, UserOperations {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            EventRepository eventRepository,
            AuthorityRepository authorityRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserResponse::fromUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        PlannerUser user = userRepository.findByEmail(s)
                .orElseThrow(() -> new UsernameNotFoundException("Email " + s + " not found"));

        return new PlannerUserDetails(user);
    }

    @Transactional
    public void mergeAdmins(List<SaveUserRequest> requests) {
        if (requests.isEmpty()) {
            return;
        }
        Map<KnownAuthority, PlannerUserAuthority> authorities = getAdminAuthorities();
        for (SaveUserRequest request : requests) {
            String email = request.email();
            PlannerUser user = userRepository.findByEmail(email).orElseGet(() -> {
                var newUser = new PlannerUser();
                newUser.setCreatedAt(OffsetDateTime.now());
                newUser.setEmail(email);
                return newUser;
            });
            user.setPassword(passwordEncoder.encode(request.password()));
            user.getAuthorities().putAll(authorities);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public UserResponse create(SaveUserRequest request) {
        validateEmailUnique(request);
        return UserResponse.fromUser(save(request, getRegularUserAuthorities()));
    }

    @Override
    @Transactional
    public UserResponse createAdmin(SaveUserRequest request) {
        validateEmailUnique(request);
        return UserResponse.fromUser(save(request, getAdminAuthorities()));
    }

    private PlannerUser save(SaveUserRequest request, Map<KnownAuthority, PlannerUserAuthority> authorities) {
        var user = new PlannerUser();
        user.getAuthorities().putAll(authorities);
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(OffsetDateTime.now());
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public UserResponse changeEmail(String email, ChangeEmailRequest request) {
        PlannerUser user = getUser(email);

        String requestEmail = request.email();
        if (!requestEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(requestEmail)) {
                throw PlannerExceptions.duplicateEmail(requestEmail);
            }
            user.setEmail(requestEmail);
        }

        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional
    public UserResponse changePasswordByEmail(String email, ChangePasswordRequest request) {
        PlannerUser user = getUser(email);
        changePassword(user, request.oldPassword(), request.newPassword());
        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw PlannerExceptions.userNotFound(email);
        }
        userRepository.deleteByEmail(email);
    }

    @Override
    @Transactional
    public UserResponse changeStatusById(Long id, UserStatus status) {
        PlannerUser user = getUser(id);
        if (user.getStatus() != status) {
            user.setStatus(status);
        }
        return UserResponse.fromUser(user);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public Page<UserResponse> list(Pageable pageable) {
//        return userRepository.findAll(pageable).map(UserResponse::fromUser);
//    }
//
//    private Map<KnownAuthority, PlannerUserAuthority> getAdminAuthorities() {
//        return authorityRepository.findAllByIdIn(AuthorityRepository.ADMIN_AUTHORITIES)
//                .collect(Collectors.toMap(
//                        PlannerUserAuthority::getId,
//                        Function.identity(),
//                        (e1, e2) -> e2,
//                        () -> new EnumMap<>(KnownAuthority.class)));
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserAdminViewResponse> list(Pageable pageable) {
        Page<PlannerUser> users = userRepository.findAll(pageable);
        Page<UserAdminViewResponse> responses = users.map(
                (user) -> UserAdminViewResponse.fromUser(user,
                                                         eventRepository.countByUser(user),
                                                         eventRepository.countByUserAndNotifyIsTrue(user),
                                                         eventRepository.countByUserAndRecurrenceIsNotNull(user)));
        return responses;
    }

    private Map<KnownAuthority, PlannerUserAuthority> getAdminAuthorities() {
        return authorityRepository.findAllByIdIn(AuthorityRepository.ADMIN_AUTHORITIES)
                .collect(Collectors.toMap(
                        PlannerUserAuthority::getId,
                        Function.identity(),
                        (e1, e2) -> e2,
                        () -> new EnumMap<>(KnownAuthority.class)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw PlannerExceptions.userNotFound(id);
        }
        userRepository.deleteById(id);
    }

    private Map<KnownAuthority, PlannerUserAuthority> getRegularUserAuthorities() {
        PlannerUserAuthority authority = authorityRepository
                .findById(KnownAuthority.ROLE_USER)
                .orElseThrow(() -> PlannerExceptions.authorityNotFound(KnownAuthority.ROLE_USER.name()));
        Map<KnownAuthority, PlannerUserAuthority> authorities = new EnumMap<>(KnownAuthority.class);
        authorities.put(KnownAuthority.ROLE_USER, authority);
        return authorities;
    }

    private void validateEmailUnique(SaveUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw PlannerExceptions.duplicateEmail(request.email());
        }
    }

    private PlannerUser getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> PlannerExceptions.userNotFound(email));
    }

    private PlannerUser getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> PlannerExceptions.userNotFound(id));
    }

    private void changePassword(PlannerUser user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw PlannerExceptions.wrongPassword();
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
