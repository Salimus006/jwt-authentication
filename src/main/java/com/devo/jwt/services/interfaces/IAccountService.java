package com.devo.jwt.services.interfaces;

import com.devo.jwt.models.AppRole;
import com.devo.jwt.models.AppUser;

import java.util.Optional;

public interface IAccountService {
    AppUser saveUser (AppUser user);
    AppRole saveRole(AppRole role);

    void addRoleToUser(String userName, String roleName);

    Optional<AppUser> findUserByUserName(String userName);

}
