package com.bogdan.todouser.resource;

import com.bogdan.todouser.domain.HttpResponse;
import com.bogdan.todouser.domain.UserPrincipal;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.enums.ErrorsEnum;
import com.bogdan.todouser.exception.CustomException;
import com.bogdan.todouser.exception.ExceptionHandling;
import com.bogdan.todouser.mapper.UserMapper;
import com.bogdan.todouser.service.UserService;
import com.bogdan.todouser.util.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.bogdan.todouser.constant.FileConstant.*;
import static com.bogdan.todouser.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.bogdan.todouser.enums.ErrorsEnum.USER_DELETED_SUCCESSFULLY;
import static com.bogdan.todouser.exception.RestResponse.createResponse;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
//refactor
public class UserResource extends ExceptionHandling {
    public static final String USER_PATH = "/users";
    public static final String USER_ID_PATH = USER_PATH + "/{userId}";
    public static final String LOGIN = USER_PATH + "/login";
    public static final String REGISTER = USER_PATH + "/register";
    public static final String IMAGE_PROFILE_USERNAME = USER_PATH + "/image/profile/{username}";
    public static final String DELETE_ID = USER_PATH + "/delete/{id}";
    public static final String RESET_PASSWORD = USER_PATH + "/reset-password/";
    private final UserService userService;
    private final AuthenticationManager manager;
    private final JWTTokenProvider tokenProvider;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserMapper userMapper;

    @Autowired
    public UserResource(UserService userService, AuthenticationManager manager
            , JWTTokenProvider tokenProvider, UserMapper userMapper) {
        this.userService = userService;
        this.manager = manager;
        this.tokenProvider = tokenProvider;
        this.userMapper = userMapper;
    }

    @PostMapping(LOGIN)
    public ResponseEntity<UserPrincipal> login(@RequestBody UserDto userDto) {
        authenticate(userDto.getUsername(), userDto.getPassword());
        UserDto loginUser = userService.findUserByUsername(userDto.getUsername())
                .orElseThrow(() -> new CustomException(ErrorsEnum.USER_NOT_FOUND));
        UserPrincipal userPrincipal = new UserPrincipal(userMapper.userDtoToUser(loginUser));
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        logger.info("User {} logged in", userDto.getUsername());

        return new ResponseEntity<>(userPrincipal, jwtHeader, ACCEPTED);

    }

    @PostMapping(REGISTER)
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {

            UserDto savedUser = userService.register(userDto);
            return new ResponseEntity<>(savedUser, CREATED);


    }


    @PostMapping(USER_ID_PATH)
    public ResponseEntity<UserDto> update(@PathVariable("userId") Long userId, @RequestBody UserDto userDto) {
        Optional<UserDto> updateUser = userService.updateUser(userId, userDto);


        return new ResponseEntity<>(updateUser.orElse(null), OK);
    }

    @GetMapping(USER_ID_PATH)
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        Optional<UserDto> userById = userService.findUserById(userId);
        return userById.orElse(null);
    }

    @GetMapping(USER_PATH)
    public List<UserDto> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping(RESET_PASSWORD + USER_ID_PATH)
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("userId") Long userId, String email) {

        UserDto userDto = userService.findUserByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorsEnum.USER_NOT_FOUND_BY_EMAIL));

        String newPassword = userService.resetPassword(userId, email);
//        return response(OK, EMAIL_SENT + email);
        return createResponse(OK, "New password " + newPassword);
    }

    @DeleteMapping(DELETE_ID)
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);

        return createResponse(NO_CONTENT, String.valueOf(USER_DELETED_SUCCESSFULLY));
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<UserDto> updateProfileImage(@RequestParam("username") String username,
                                                      @RequestParam(value = "profileImage") MultipartFile profileImage)
            throws IOException {

        UserDto user = userService.updateProfileImage(username, profileImage);

        return new ResponseEntity<>(user, OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
            throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = IMAGE_PROFILE_USERNAME, produces = IMAGE_JPEG_VALUE)
    public byte[] getTemporaryProfileImage(@PathVariable("username") String username) throws IOException {

        if (userService.findUserByUsername(username).isEmpty()) {
            throw new CustomException(ErrorsEnum.USER_NOT_FOUND);
        } else {
            URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try (InputStream inputStream = url.openStream()) {
                int bytesRead;
                byte[] chunk = new byte[1024];

                while ((bytesRead = inputStream.read(chunk)) > 0) {
                    outputStream.write(chunk, 0, bytesRead);
                }
            }
            return outputStream.toByteArray();
        }
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, tokenProvider.generateJwtToken(userPrincipal));

        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
