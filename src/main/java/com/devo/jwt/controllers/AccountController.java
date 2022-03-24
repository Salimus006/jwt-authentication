package com.devo.jwt.controllers;

import com.devo.jwt.dto.RegisterForm;
import com.devo.jwt.models.AppUser;
import com.devo.jwt.services.interfaces.IAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
