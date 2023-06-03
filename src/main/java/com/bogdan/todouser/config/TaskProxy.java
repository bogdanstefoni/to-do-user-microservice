package com.bogdan.todouser.config;

import com.bogdan.todouser.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@FeignClient(name = "to-do-task", url = "localhost:8200")
@FeignClient(name = "to-do-app", url = "${TO_DO_APP_SERVICE_HOST:localhost}:8200")
@Configuration
public interface TaskProxy {

    @GetMapping("/tasks/{taskName}")
    ResponseEntity<TaskDto> findTaskByName(@PathVariable String taskName);

    @GetMapping("/{id}")
    ResponseEntity<TaskDto> findById(@PathVariable Long id);

    @PostMapping("/tasks/{userId}/create")
    ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto, @PathVariable Long userId);

    @PutMapping("/tasks/{userId}/update")
    ResponseEntity<TaskDto> updateTask(@RequestBody TaskDto taskDto, @PathVariable Long userId);

    @GetMapping("/tasks/list")
    ResponseEntity<List<TaskDto>> getAllTasks();

    @GetMapping("/tasks/list/{userId}")
    List<TaskDto> findTasksByUserId(@PathVariable Long userId);

    @DeleteMapping("/task/{id}")
    void deleteTask(@PathVariable Long id);

}
