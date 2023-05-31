package com.bogdan.todouser.service;

import com.bogdan.todouser.domain.User;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.exception.EmailExistException;
import com.bogdan.todouser.exception.EmailNotFoundException;
import com.bogdan.todouser.exception.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto register(UserDto user);

    List<UserDto> getUsers();

    Optional<UserDto> findUserById(Long id);

    Optional<UserDto> findUserByUsername(String username);

    Optional<UserDto> findUserByEmail(String email);

    Optional<UserDto> updateUser(Long userId, UserDto user);
    Boolean deleteUser(Long id);

    String resetPassword(Long id, String email) throws EmailNotFoundException;

    UserDto updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException;
}
