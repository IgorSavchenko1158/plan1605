package com.example.plan1605.repository;

import com.example.plan1605.model.user.KnownAuthority;
import com.example.plan1605.model.user.PlannerUserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

public interface AuthorityRepository extends JpaRepository<PlannerUserAuthority, KnownAuthority> {
    Set<KnownAuthority> ADMIN_AUTHORITIES = EnumSet.of(KnownAuthority.ROLE_USER, KnownAuthority.ROLE_ADMIN);

    Stream<PlannerUserAuthority> findAllByIdIn(Set<KnownAuthority> ids);
}
