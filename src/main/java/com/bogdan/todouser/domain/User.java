package com.bogdan.todouser.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private String username;

    private String password;

    private Date lastLoginDate;
    private Date lastLoginDateDisplay;

    private String role;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;
    private String profileImageUrl;
    private String port;

}
