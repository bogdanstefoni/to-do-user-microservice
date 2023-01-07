package com.bogdan.todouser.controller;

import com.bogdan.todouser.config.TaskProxy;
import com.bogdan.todouser.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/tasks/")
public class UserTaskController {

    @Autowired
    private TaskProxy proxy;

    @PostMapping("/")
    public ResponseEntity<String> createTask(@RequestBody TaskDto taskDto) {

        return proxy.createTask(taskDto);

    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getTaskById(@PathVariable Long id) {

        return proxy.getTasksById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTaskById(@RequestBody TaskDto taskDto, @PathVariable Long id) {

        return proxy.updateTask(taskDto, id);
    }

}
