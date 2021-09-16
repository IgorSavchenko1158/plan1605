package com.example.plan1605.repository;

import com.example.plan1605.model.user.PlannerUser;
import com.example.plan1605.model.user.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<PlannerUser, Long> {

    Optional<PlannerUser> findByEmail(String email);

    @Query("update PlannerUser u set u.status = :status where u.email = :email")
    @Modifying
    void changeStatusByEmail(String email, UserStatus status);

    boolean existsByEmail(String email);

    @Modifying
    void deleteByEmail(String email);
}

