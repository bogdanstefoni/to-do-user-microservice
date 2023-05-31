package com.bogdan.todouser.service.impl;

import com.bogdan.todouser.domain.User;
import com.bogdan.todouser.domain.UserPrincipal;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.enums.Role;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.bogdan.todouser.constant.FileConstant.*;
import static com.bogdan.todouser.enums.ErrorsEnum.USER_NOT_FOUND;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

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
        return userMapper.userToUserDto(userRepository.save(userMapper.userDtoToUser(userDto)));
    }

    @Override
    public Optional<UserDto> findUserById(Long id)  {
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
        return Optional.ofNullable(userMapper.userToUserDto(
                userRepository.findUserByUsername(username)));
    }

    @Override
    public Optional<UserDto> findUserByEmail(String email) {
        return Optional.ofNullable(userMapper.userToUserDto(
                userRepository.findUserByEmail(email)));
    }


    @Override
    public Optional<UserDto> updateUser(Long userId, UserDto user) {
        AtomicReference<Optional<UserDto>> atomicReference = new AtomicReference<>();

        userRepository.findById(userId).ifPresentOrElse(foundUser -> {
            foundUser.setFirstName(user.getFirstName());
            foundUser.setLastName(user.getLastName());
            foundUser.setEmail(user.getEmail());
            foundUser.setUsername(user.getUsername());
            foundUser.setPassword(user.getPassword());
            atomicReference.set(Optional.of(userMapper.userToUserDto(userRepository.save(foundUser))));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    public Boolean deleteUser(Long id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public String resetPassword(Long id, String email)  {
        AtomicReference<Optional<UserDto>> atomicReference = new AtomicReference<>();

        String password = generatePassword();
        userRepository.findById(id).ifPresentOrElse(user -> {
            user.setPassword(encodePassword(password));
            atomicReference.set(Optional.of(userMapper.userToUserDto(userRepository.save(user))));
        }, () -> atomicReference.set(Optional.empty()));

        return password;
//        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
    }

    @Override
    public UserDto updateProfileImage(String username, MultipartFile profileImage) throws IOException {
        UserDto userDto = userMapper.userToUserDto(userRepository.findUserByUsername(username));
        boolean isNewUsernameAndPasswordValid = validateNewUsernameAndEmail(username, null, null);

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
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private boolean validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) {

        if (isNoneBlank(currentUsername)) {
            Optional<UserDto> currentUser = findUserByUsername(currentUsername);
            if (currentUser.isEmpty()) {
                return false;
            }
            if(isUsernameTaken(currentUsername, newUsername)) {
                return false;
            }

            return !isEmailTaken(currentUsername, newEmail);
        } else {
            if (isEmailTaken(newUsername, null)) {
                return false;
            }
            return !isUsernameTaken(newUsername, null);
        }
    }

    private boolean isUsernameTaken(String currentUsername, String newUsername){

        Optional<UserDto> userByNewUsername = findUserByUsername(newUsername);
        return userByNewUsername.isEmpty() || currentUsername.equals(userByNewUsername.get().getUsername());
    }

    private boolean isEmailTaken(String currentUsername, String newEmail) {

        Optional<UserDto> userByNewEmail = findUserByEmail(newEmail);
        return userByNewEmail.isEmpty() || currentUsername.equals(userByNewEmail.get().getEmail());
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
