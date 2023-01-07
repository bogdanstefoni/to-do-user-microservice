package com.bogdan.todouser.config;

import com.bogdan.todouser.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "to-do-task", url = "localhost:8000")
@Configuration
public interface TaskProxy {

    @GetMapping("/tasks/{id}")
    ResponseEntity<String> getTasksById(@PathVariable Long id);

    @PostMapping("/tasks/")
    ResponseEntity<String> createTask(@RequestBody TaskDto taskDto);

    @PutMapping("/tasks/update/{taskId}")
    ResponseEntity<String> updateTask(@RequestBody TaskDto taskDto, @PathVariable Long taskId);


}
