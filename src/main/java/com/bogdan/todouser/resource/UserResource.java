package com.bogdan.todouser.resource;

import com.bogdan.todouser.domain.HttpResponse;
import com.bogdan.todouser.domain.User;
import com.bogdan.todouser.domain.UserPrincipal;
import com.bogdan.todouser.exception.EmailExistException;
import com.bogdan.todouser.exception.EmailNotFoundException;
import com.bogdan.todouser.exception.ExceptionHandling;
import com.bogdan.todouser.exception.UsernameExistException;
import com.bogdan.todouser.service.UserService;
import com.bogdan.todouser.util.JWTTokenProvider;
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

import static com.bogdan.todouser.constant.FileConstant.*;
import static com.bogdan.todouser.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.bogdan.todouser.enums.ErrorsEnum.USER_DELETED_SUCCESSFULLY;
import static com.bogdan.todouser.factory.RestResponseFactory.createResponse;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/", "/users"})
public class UserResource extends ExceptionHandling {
    private final UserService userService;
    private final AuthenticationManager manager;
    private final JWTTokenProvider tokenProvider;

    @Autowired
    public UserResource(UserService userService, AuthenticationManager manager, JWTTokenProvider tokenProvider) {
        this.userService = userService;
        this.manager = manager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);

        return new ResponseEntity<>(loginUser, jwtHeader, OK);

    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UsernameExistException, EmailExistException {
        User userRegister = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getPassword());

        return new ResponseEntity<>(userRegister, OK);
    }


    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestParam("currentUser") String currentUser,
                                       @RequestParam("firstName") String firstName,
                                       @RequestParam("lastName") String lastName,
                                       @RequestParam("username") String username,
                                       @RequestParam("password") String password,
                                       @RequestParam("email") String email,
                                       @RequestParam("role") String role,
                                       @RequestParam("isActive") String isActive,
                                       @RequestParam("isNonLocked") String isNonLocked,
                                       @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws EmailExistException, IOException, UsernameExistException {

        User updatedUser = userService.updateUser(currentUser, firstName, lastName, username, password, email, role,
                Boolean.parseBoolean(isActive), Boolean.parseBoolean(isNonLocked), profileImage);

        return new ResponseEntity<>(updatedUser, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findUserByUsername(username);

        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();

        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/reset-password/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {

        User user = userService.findUserByEmail(email);
        user.setPassword(userService.resetPassword(email));
//        return response(OK, EMAIL_SENT + email);
        return createResponse(OK, "New password " + user.getPassword());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);

        return createResponse(NO_CONTENT, String.valueOf(USER_DELETED_SUCCESSFULLY));
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,
                                                   @RequestParam(value = "profileImage") MultipartFile profileImage) throws EmailExistException, IOException, UsernameExistException {

        User user = userService.updateProfileImage(username, profileImage);

        return new ResponseEntity<>(user, OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
            throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTemporaryProfileImage(@PathVariable("username") String username) throws IOException {

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

//    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
//        HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus,
//                httpStatus.getReasonPhrase().toUpperCase(), message);
//
//        return new ResponseEntity<>(body, httpStatus);
//    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, tokenProvider.generateJwtToken(userPrincipal));

        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
