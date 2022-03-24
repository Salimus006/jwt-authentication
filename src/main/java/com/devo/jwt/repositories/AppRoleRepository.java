package com.devo.jwt.repositories;

import com.devo.jwt.models.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

    /**
     * search a role by name
     *
     * @param roleName role to search
     * @return AppRole
     */
    Optional<AppRole> findByRoleName(String roleName);
}
