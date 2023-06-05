package com.bogdan.todouser.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String profileImageUrl;
    private String[] authorities;
    private String role;
    private boolean isActive;
    private boolean isNotLocked;
    private List<TaskDto> tasks;
}
