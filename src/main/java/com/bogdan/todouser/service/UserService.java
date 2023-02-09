package com.bogdan.todouser.service;

import com.bogdan.todouser.domain.User;
import com.bogdan.todouser.exception.EmailExistException;
import com.bogdan.todouser.exception.EmailNotFoundException;
import com.bogdan.todouser.exception.UserNotFoundException;
import com.bogdan.todouser.exception.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email, String password) throws EmailExistException, UsernameExistException;

    List<User> getUsers();

    User findUserById(long id) throws UserNotFoundException;

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
                    String newPassword, String newEmail, String role, boolean isNonLocked,
                    boolean isActive, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException;

    void deleteUser(long id);

    String resetPassword(String email) throws EmailNotFoundException;

    User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException;
}
