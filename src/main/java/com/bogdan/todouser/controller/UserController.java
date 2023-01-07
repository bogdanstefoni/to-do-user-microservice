package com.bogdan.todouser.controller;

import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.entity.UserEntity;
import com.bogdan.todouser.service.UserControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserControlService userControlService;


    @GetMapping("/")
    public ResponseEntity<String> getUsers() {

        return userControlService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> findById(@PathVariable Long id) {
        return userControlService.findUserById(id);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<String> findByUsername(@PathVariable String username) {

        return userControlService.findByUsername(username);
    }

    @PostMapping("/")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDto userDto) {

        return userControlService.register(userDto);
    }

    @PostMapping("/login/")
    public ResponseEntity<String> loginUser(@Valid @RequestBody UserDto userDto) {

        return userControlService.login(userDto);
    }

    @PutMapping("/")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto, @PathVariable Long id) {
        userDto.setId(id);

        return userControlService.update(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(Long id) {

        userControlService.deleteById(id);
    }

}
