package com.example.plan1605.config.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "planner.security")
public class PlannerSecurityProperties {

    @Valid
    @NestedConfigurationProperty
    private PlannerJWTProperties jwt;

    private Map<@NotBlank String, @Valid PlannerAdminProperties> admins;

    public PlannerJWTProperties getJwt() {
        return jwt;
    }

    public void setJwt(PlannerJWTProperties jwt) {
        this.jwt = jwt;
    }

    public Map<String, PlannerAdminProperties> getAdmins() {
        return admins;
    }

    public void setAdmins(Map<String, PlannerAdminProperties> admins) {
        this.admins = admins;
    }
}
