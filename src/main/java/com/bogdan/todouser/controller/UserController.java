package com.bogdan.todouser.controller;

import com.bogdan.todouser.entity.UserEntity;
import com.bogdan.todouser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private ItemProxy proxy;


    @GetMapping("/")
    public List<UserEntity> getUsers() {

        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<UserEntity> findById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    @PostMapping("/")
    public UserEntity createUser(@RequestBody UserEntity userEntity) {


        return userRepository.save(userEntity);
    }

//    @PostMapping("/tasks/")
//    public ResponseEntity<String> createTask(@RequestBody Task task) {
//
//        return proxy.createTask(task);
//
//    }
//
//    @GetMapping("/tasks/{id}")
//    public ResponseEntity<String> getTaskById(@PathVariable Long id) {
//
//        return proxy.getTasksById(id);
//    }
//
//    @PutMapping("/tasks/{id}")
//    public ResponseEntity<String> updateTaskById(@RequestBody Task task, @PathVariable Long id){
//
//        return proxy.updateTask(task, id);
//    }


}
