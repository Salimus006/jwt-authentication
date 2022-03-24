package com.devo.jwt.services;

import com.devo.jwt.models.AppUser;
import com.devo.jwt.repositories.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final AppUserRepository repository;

    public UserService(AppUserRepository repository) {
        this.repository = repository;
    }

    public List<AppUser> findAll(){
        return repository.findAll();
    }

    public Optional<AppUser> findById(Long id){
        return repository.findById(id);
    }

    public AppUser save(AppUser user){
        return repository.save(user);
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }

    public List<AppUser> findByFirstName(String firstName){
        return repository.findByFirstName(firstName);
    }
}
