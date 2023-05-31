package com.bogdan.todouser.resource;

import com.bogdan.todouser.config.TaskProxy;
import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.exception.UserNotFoundException;
import com.bogdan.todouser.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/users/tasks/")
public class UserTaskController {

    @Autowired
    private TaskProxy proxy;

    @Autowired
    private TaskService taskService;

    @GetMapping("/list/{userId}")
    public ResponseEntity<UserDto> getTasksByUserId(@PathVariable long userId) throws UserNotFoundException, IOException {
        UserDto responseDto = taskService.findTasksByUserId(userId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/find/{taskName}")
    public ResponseEntity<TaskDto> findTaskByName(@PathVariable String taskName) {
        return proxy.findTaskByName(taskName);
    }

    @PostMapping("/{userId}/create")
    public ResponseEntity<UserDto> createTask(@RequestBody TaskDto taskDto, @PathVariable long userId) throws UserNotFoundException, JsonProcessingException {
        UserDto responseDto = taskService.createTask(taskDto, userId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<UserDto> updateTask(@RequestBody TaskDto taskDto, @PathVariable long userId) throws UserNotFoundException {
        UserDto responseDto = taskService.updateTask(taskDto, userId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
