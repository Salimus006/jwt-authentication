package com.devo.jwt.repositories;

import com.devo.jwt.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * search a user by userName
     *
     * @param userName unique identifier
     * @return a user
     */
    Optional<AppUser> findByUserName(String userName);

    /**
     * Retrun list of users matching with the userName
     *
     * @param firstName user's first name
     * @return List<User>
     */
    List<AppUser> findByFirstName(String firstName);
}
