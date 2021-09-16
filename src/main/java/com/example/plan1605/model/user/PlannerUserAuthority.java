package com.example.plan1605.model.user;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authorities")
public class PlannerUserAuthority {

    @Id
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.ORDINAL)
    private KnownAuthority id;

    @ManyToMany(mappedBy = "authorities")
    private Set<PlannerUser> users = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlannerUserAuthority that = (PlannerUserAuthority) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public KnownAuthority getId() {
        return id;
    }

    public void setId(KnownAuthority id) {
        this.id = id;
    }

    public Set<PlannerUser> getUsers() {
        return users;
    }
}
