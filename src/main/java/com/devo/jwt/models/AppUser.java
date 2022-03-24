package com.devo.jwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to manage user's roles
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userName;

    @NotBlank
    @Size(min = 1, max = 250)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 250)
    private String lastName;
    private LocalDate birth;

    @JsonIgnore
    private String password;

    /**
     * User can have many roles
     */
    @ManyToMany(fetch = FetchType.EAGER)
    Set<AppRole> roles = new HashSet<>();
}
