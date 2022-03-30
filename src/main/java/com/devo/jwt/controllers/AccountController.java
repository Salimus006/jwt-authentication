package com.devo.jwt.controllers;

import com.devo.jwt.dto.RegisterForm;
import com.devo.jwt.exceptions.RoleNotFoundException;
import com.devo.jwt.exceptions.UserNotFoundException;
import com.devo.jwt.models.AppUser;
import com.devo.jwt.services.interfaces.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/register")
@Tag(name = "Accounts", description = "User's account")
public class AccountController {

    private final IAccountService accountService;

    public AccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AppUser register(@Valid @RequestBody RegisterForm form){
        return this.accountService.saveUser(form.buildAppUser());
    }

    @Operation(summary = "Ajout d'un rôle à un utilisateur", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/role/{userName}/{roleName}")
    public ResponseEntity<AppUser> addRoleToUser(@PathVariable String userName, @PathVariable String roleName){
        // Check that the role exists
        try{
            return ResponseEntity.ok(this.accountService.addRoleToUser(userName, roleName));
        }catch (UserNotFoundException | RoleNotFoundException e){
            System.err.println("Utilisateur ou rôle inexistant userName : " + userName + " roleName : " + roleName);
            return ResponseEntity.notFound().build();
        }
    }
}
