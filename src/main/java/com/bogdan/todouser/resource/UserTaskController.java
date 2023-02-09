package com.bogdan.todouser.resource;

import com.bogdan.todouser.config.TaskProxy;
import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.dto.UserResponseDto;
import com.bogdan.todouser.exception.UserNotFoundException;
import com.bogdan.todouser.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users/tasks/")
public class UserTaskController {

    @Autowired
    private TaskProxy proxy;

    @Autowired
    private TaskService taskService;

    @GetMapping("/list/{userId}")
    public ResponseEntity<UserResponseDto> getTasksByUserId(@PathVariable long userId) throws UserNotFoundException, IOException {
        return taskService.findTasksByUserId(userId);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TaskDto>> getUserTasks() throws IOException {
        return taskService.getAllTasks();
    }

    @GetMapping("/find/{taskName}")
    public ResponseEntity<List<TaskDto>> findTaskByName(@PathVariable String taskName) {
        return proxy.findTaskByName(taskName);
    }

    @PostMapping("/{userId}/create")
    public ResponseEntity<UserResponseDto> createTask(@RequestBody TaskDto taskDto, @PathVariable long userId) throws UserNotFoundException, JsonProcessingException {

        return taskService.createTask(taskDto, userId);

    }

    @PutMapping("/{taskName}")
    public ResponseEntity<TaskDto> updateTaskById(@RequestBody TaskDto taskDto, @PathVariable String taskName) {

        return proxy.updateTask(taskDto, taskName);
    }

}
