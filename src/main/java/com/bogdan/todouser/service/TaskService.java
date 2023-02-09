package com.bogdan.todouser.service;

import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.dto.UserResponseDto;
import com.bogdan.todouser.exception.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;


public interface TaskService {

    ResponseEntity<UserResponseDto> findTasksByUserId(long userId) throws UserNotFoundException, IOException;

    ResponseEntity<UserResponseDto> createTask(TaskDto taskDto, long id) throws UserNotFoundException, JsonProcessingException;

    ResponseEntity<List<TaskDto>> getAllTasks() throws IOException;
}
