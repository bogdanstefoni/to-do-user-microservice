package com.bogdan.todouser.service;

import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.dto.UserResponseDto;
import com.bogdan.todouser.exception.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;


public interface TaskService {

    UserResponseDto findTasksByUserId(long userId) throws UserNotFoundException, IOException;

    UserResponseDto createTask(TaskDto taskDto, long id) throws UserNotFoundException, JsonProcessingException;

    UserResponseDto updateTask(TaskDto taskDto, long userId) throws UserNotFoundException;
}
