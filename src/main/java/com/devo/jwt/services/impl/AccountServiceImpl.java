package com.devo.jwt.services.impl;

import com.devo.jwt.exceptions.RoleNotFoundException;
import com.devo.jwt.exceptions.UserAlreadyExistsException;
import com.devo.jwt.exceptions.UserNotFoundException;
import com.devo.jwt.models.AppRole;
import com.devo.jwt.models.AppUser;
import com.devo.jwt.repositories.AppRoleRepository;
import com.devo.jwt.repositories.AppUserRepository;
import com.devo.jwt.services.interfaces.IAccountService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service allow to :
 * Add a new user (and encode password with BCryptPasswordEncoder)
 * Add a new role (USER, ADMIN)
 * Add role to User
 */
@Service
@Transactional
public class AccountServiceImpl implements IAccountService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;

    public AccountServiceImpl(BCryptPasswordEncoder passwordEncoder, AppUserRepository userRepository, AppRoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public AppUser saveUser(AppUser user) {
        this.userRepository.findByUserName(user.getUserName()).ifPresent(u -> {
            throw new UserAlreadyExistsException(String.format("user already exists with userName %s", user.getUserName()));
        });
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public AppRole saveRole(AppRole role) {
        return this.roleRepository.save(role);
    }

    @Override
    public AppUser addRoleToUser(String userName, String roleName) {
        // first get role by name
        AppRole role = this.roleRepository.findByRoleName(roleName).orElseThrow(RoleNotFoundException::new);
        AppUser user = this.userRepository.findByUserName(userName).orElseThrow(UserNotFoundException::new);

        user.getRoles().add(role);

        return user;
    }

    @Override
    public Optional<AppUser> findUserByUserName(String userName) {
        return this.userRepository.findByUserName(userName);
    }
}
