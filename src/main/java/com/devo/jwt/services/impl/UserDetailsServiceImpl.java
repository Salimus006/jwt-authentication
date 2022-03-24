package com.devo.jwt.services.impl;

import com.devo.jwt.models.AppRole;
import com.devo.jwt.models.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Impl of UserDetailsService and override of method (loadUserByUsername(userName)) used by spring to load a user (userName
 * and password)
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountServiceImpl accountService;

    public UserDetailsServiceImpl(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser user = this.accountService.findUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("user not found with userName %s", username)));

        return new User(user.getUserName(), user.getPassword(), buildAuthorities(user.getRoles()));
    }

    private static Collection<GrantedAuthority> buildAuthorities(Set<AppRole> roles){
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        roles.forEach(appRole -> authorities.add(new SimpleGrantedAuthority(appRole.getRoleName())));

        return authorities;
    }
}
