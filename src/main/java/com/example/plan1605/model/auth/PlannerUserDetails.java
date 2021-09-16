package com.example.plan1605.model.auth;

import com.example.plan1605.model.user.PlannerUser;
import com.example.plan1605.model.user.UserStatus;
import org.springframework.security.core.userdetails.User;

import java.util.EnumSet;

public class PlannerUserDetails extends User {

    private final PlannerUser source;

    public PlannerUserDetails(PlannerUser source) {
        super(source.getEmail(),
              source.getPassword(),
              source.getStatus() == UserStatus.ACTIVE,
              true,
              true,
              true,
              EnumSet.copyOf(source.getAuthorities().keySet()));
        this.source = source;
    }

    public PlannerUser getSource() {
        return source;
    }
}
