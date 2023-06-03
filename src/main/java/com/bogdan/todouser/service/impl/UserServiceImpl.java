package com.bogdan.todouser.service.impl;

import com.bogdan.todouser.domain.User;
import com.bogdan.todouser.domain.UserPrincipal;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.enums.ErrorsEnum;
import com.bogdan.todouser.enums.Role;
import com.bogdan.todouser.exception.CustomException;
import com.bogdan.todouser.mapper.UserMapper;
import com.bogdan.todouser.repository.UserRepository;
import com.bogdan.todouser.service.LoginAttemptService;
import com.bogdan.todouser.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bogdan.todouser.constant.FileConstant.*;
import static com.bogdan.todouser.enums.ErrorsEnum.USER_NOT_FOUND;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional
@Qualifier("userDetailsService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final Environment environment;
    private final UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            logger.error(USER_NOT_FOUND + username);
            throw new UsernameNotFoundException(USER_NOT_FOUND + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDateDisplay());
            user.setLastLoginDate(new Date());
            userRepository.save(user);

            UserPrincipal userPrincipal = new UserPrincipal(user);

            logger.info("Returning user by username: " + username);

            return userPrincipal;
        }

    }


    @Override
    public UserDto register(UserDto userDto) {
        validateUserNotExists(userDto.getUsername(), userDto.getEmail());

        User user = createUserFromDto(userDto);
        User savedUser = userRepository.save(user);

        return userMapper.userToUserDto(savedUser);
    }

    private User createUserFromDto(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);

        user.setPassword(encodePassword(userDto.getPassword()));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());

        return user;
    }

    private void validateUserNotExists(String username, String email) {
        Optional<UserDto> userByUsername = findUserByUsername(username);
        if (userByUsername.isPresent()) {
            throw new CustomException(ErrorsEnum.USER_EXISTS);
        }

        Optional<UserDto> userByEmail = findUserByEmail(email);
        if (userByEmail.isPresent()) {
            throw new CustomException(ErrorsEnum.EMAIL_ALREADY_EXISTS);
        }
    }


    @Override
    public Optional<UserDto> findUserById(Long id) {
        return Optional.ofNullable(userMapper.userToUserDto(
                userRepository.findById(id).orElse(null)));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        return Optional.ofNullable(user)
                .map(userMapper::userToUserDto);
    }


    @Override
    public Optional<UserDto> findUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        return Optional.ofNullable(user)
                .map(userMapper::userToUserDto);
    }


    @Override
    public Optional<UserDto> updateUser(Long userId, UserDto user) {
        return userRepository.findById(userId)
                .flatMap(foundUser -> {
                    foundUser.setFirstName(user.getFirstName());
                    foundUser.setLastName(user.getLastName());
                    foundUser.setEmail(user.getEmail());
                    foundUser.setUsername(user.getUsername());
                    foundUser.setPassword(user.getPassword());
                    userRepository.save(foundUser);
                    return Optional.ofNullable(userMapper.userToUserDto(foundUser));
                });
    }

    @Override
    public Boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public String resetPassword(Long id, String email) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        String password = generatePassword();
        user.setPassword(encodePassword(password));
        return password;
    }

    @Override
    public UserDto updateProfileImage(String username, MultipartFile profileImage) throws IOException {
        UserDto userDto = userMapper.userToUserDto(userRepository.findUserByUsername(username));
        boolean isNewUsernameAndPasswordValid = isUsernameAndEmailValid(username, null, null);

        if (isNewUsernameAndPasswordValid && profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER, userDto.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                logger.info(DIRECTORY_CREATED + userFolder);
            }

            Files.deleteIfExists(userFolder.resolve(userDto.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(userDto.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);

            userDto.setProfileImageUrl(setProfileImageUrl(userDto.getUsername()));
            userRepository.save(userMapper.userDtoToUser(userDto));
        }
        return userDto;
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH
                + username + DOT + JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String getTemporaryProfileImage(String username) {
        if (isUsernameAndEmailValid(username, null, null)) {
            return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
        }
        return new CustomException(USER_NOT_FOUND).getMessage();
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private boolean isUsernameAndEmailValid(String existingUsername, String proposedUsername, String proposedEmail) {
        if (isNotBlank(existingUsername)) {
            Optional<UserDto> currentUser = findUserByUsername(existingUsername);
            if (currentUser.isEmpty()) {
                return false;
            }
            if (isUsernameTaken(existingUsername, proposedUsername)) {
                return false;
            }
            return !isEmailTaken(existingUsername, proposedEmail);
        } else {
            if (isBlank(proposedEmail)) {
                return false;
            }
            if (isEmailTaken(proposedEmail)) {
                return false;
            }
            return !isUsernameTaken(proposedUsername);
        }
    }

    private boolean isUsernameTaken(String existingUsername, String proposedUsername) {
        Optional<UserDto> userByProposedUsername = findUserByUsername(proposedUsername);
        return userByProposedUsername.isPresent()
                && !existingUsername.equals(userByProposedUsername.get().getUsername());
    }

    private boolean isEmailTaken(String existingUsername, String proposedEmail) {
        Optional<UserDto> userByProposedEmail = findUserByEmail(proposedEmail);
        return userByProposedEmail.isPresent()
                && !existingUsername.equals(userByProposedEmail.get().getEmail());
    }

    private boolean isUsernameTaken(String username) {
        Optional<UserDto> userDto = findUserByUsername(username);
        return userDto.isPresent();
    }

    private boolean isEmailTaken(String email) {
        Optional<UserDto> userDto = findUserByEmail(email);
        return userDto.isPresent();
    }

    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    private String registerPort() {
        return environment.getProperty("local.server.port");
    }


}
