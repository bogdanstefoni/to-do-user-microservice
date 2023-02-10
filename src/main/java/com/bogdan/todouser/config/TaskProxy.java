package com.bogdan.todouser.config;

import com.bogdan.todouser.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@FeignClient(name = "to-do-task", url = "localhost:8000")
@FeignClient(name = "to-do-app")
@Configuration
public interface TaskProxy {

    @GetMapping("/tasks/{taskName}")
    ResponseEntity<TaskDto> findTaskByName(@PathVariable String taskName);

    @PostMapping("/tasks/{userId}/create")
    ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto, @PathVariable Long userId);

    @PutMapping("/tasks/{userId}/update")
    ResponseEntity<TaskDto> updateTask(@RequestBody TaskDto taskDto, @PathVariable Long userId);

    @GetMapping("/tasks/list")
    ResponseEntity<List<TaskDto>> getAllTasks();

    @GetMapping("/tasks/list/{userId}")
    List<TaskDto> findTasksByUserId(@PathVariable Long userId);

}
