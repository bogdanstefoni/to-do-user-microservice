package com.bogdan.todouser.service;

import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.exception.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;


public interface TaskService {

    UserDto findTasksByUserId(long userId) throws UserNotFoundException, IOException;

    UserDto createTask(TaskDto taskDto, long id) throws UserNotFoundException, JsonProcessingException;

    UserDto updateTask(TaskDto taskDto, long userId) throws UserNotFoundException;
}
