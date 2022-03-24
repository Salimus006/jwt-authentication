package com.devo.jwt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User's roles
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppRole {

    @Id
    @GeneratedValue
    private Long id;
    private String roleName;
}
