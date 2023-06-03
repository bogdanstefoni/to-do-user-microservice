package com.bogdan.todouser.resource;

import com.bogdan.todouser.config.TaskProxy;
import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.exception.TaskNotFoundException;
import com.bogdan.todouser.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.bogdan.todouser.resource.UserTaskController.TASKS_PATH;

@RestController
@RequestMapping(TASKS_PATH)
@RequiredArgsConstructor
public class UserTaskController {

    public static final String TASKS_PATH = "/users/tasks/";
    public static final String LIST_USER_ID = "/list/{userId}";
    public static final String FIND_TASK_TASK_NAME = "/find-task/{taskName}";
    public static final String USER_ID_CREATE_TASK = "/{userId}/create-task";
    public static final String USER_ID_UPDATE_TASK = "/{userId}/update-task";
    public static final String DELETE_TASK_TASK_ID = "/delete-task/{taskId}";

    private final TaskProxy proxy;

    private final TaskService taskService;

    @GetMapping(LIST_USER_ID)
    public UserDto getTasksByUserId(@PathVariable long userId) {
        return taskService.findTasksByUserId(userId).orElse(null);
    }

    @GetMapping(FIND_TASK_TASK_NAME)
    public ResponseEntity<TaskDto> findTaskByName(@PathVariable String taskName) {
        return proxy.findTaskByName(taskName);
    }

    @PostMapping(USER_ID_CREATE_TASK)
    public ResponseEntity<UserDto> createTask(@RequestBody TaskDto taskDto, @PathVariable long userId) {
        UserDto responseDto = taskService.createTask(taskDto, userId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);

    }

    @PutMapping(USER_ID_UPDATE_TASK)
    public ResponseEntity<UserDto> updateTask(@RequestBody TaskDto taskDto, @PathVariable long userId) {
        UserDto responseDto = taskService.updateTask(taskDto, userId);
        return new ResponseEntity<>(responseDto, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(DELETE_TASK_TASK_ID)
    public ResponseEntity deleteTask(@PathVariable("taskId") Long taskId) {
        if(!taskService.deleteTask(taskId)) {
            throw new TaskNotFoundException();
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
