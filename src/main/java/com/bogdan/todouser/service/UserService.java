package com.bogdan.todouser.service;

import com.bogdan.todouser.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto register(UserDto userDto);

    List<UserDto> getUsers();

    Optional<UserDto> findUserById(Long id);


    Optional<UserDto> findUserByUsername(String username);

    Optional<UserDto> findUserByEmail(String email);

    Optional<UserDto> updateUser(Long userId, UserDto userDto);

    Boolean deleteUser(Long id);

    String resetPassword(Long id, String email);

    UserDto updateProfileImage(String username, MultipartFile profileImage) throws IOException;
}
