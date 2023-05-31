package com.bogdan.todouser.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String profileImageUrl;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;
}
