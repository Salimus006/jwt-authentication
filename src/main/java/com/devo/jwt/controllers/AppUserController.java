package com.devo.jwt.controllers;

import com.devo.jwt.exceptions.UserNotFoundException;
import com.devo.jwt.models.AppUser;
import com.devo.jwt.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User's CRUD operations")
public class AppUserController {

    private final UserService service;

    public AppUserController(UserService service) {
        this.service = service;
    }

    @PostMapping()
    public EntityModel<AppUser> saveUser(@Valid @RequestBody AppUser user) {
        // Links
        // 1 self link
        Link selfLink = linkTo(methodOn(this.getClass()).saveUser(user)).withSelfRel();
        Link update = linkTo(methodOn(this.getClass()).putUser(user.getId(), user)).withRel("update");
        Link aggregateRoot = linkTo(methodOn(this.getClass()).all()).withRel("users");

        return EntityModel.of(service.save(user), selfLink, update, aggregateRoot);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> findById(@PathVariable long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElseThrow(UserNotFoundException::new);
    }

    @GetMapping()
    public ResponseEntity<List<AppUser>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AppUser> putUser(
            @PathVariable("id") final Long id, @RequestBody final AppUser user) {
        service.findById(id).orElseThrow(UserNotFoundException::new);
        return ResponseEntity.ok(service.save(user));
    }

    @RolesAllowed("ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        //service.findById(id).ifPresentOrElse(() -> service::deleteById);
        try {
            service.deleteById(id);
            return ResponseEntity.ok().build();
        }catch (EmptyResultDataAccessException e){
            return ResponseEntity.notFound().build();
        }
    }
}
