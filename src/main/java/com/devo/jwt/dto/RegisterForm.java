package com.devo.jwt.dto;

import com.devo.jwt.exceptions.InvalidPassWordException;
import com.devo.jwt.models.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterForm {

    private String userName;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private LocalDate birth;

    @JsonIgnore
    public AppUser buildAppUser(){

        if (!StringUtils.hasLength(this.password) || !StringUtils.hasLength(this.confirmPassword)
                || ! this.password.equals(this.confirmPassword)){
            throw new InvalidPassWordException("Invalid password");
        }
        return AppUser.builder()
                .userName(this.userName)
                .password(this.password)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .birth(this.birth)
                .build();
    }

}
