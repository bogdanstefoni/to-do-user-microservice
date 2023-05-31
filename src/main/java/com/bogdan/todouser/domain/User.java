package com.bogdan.todouser.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;



@Setter
@Getter
@Entity
public class User {

    @Id
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
