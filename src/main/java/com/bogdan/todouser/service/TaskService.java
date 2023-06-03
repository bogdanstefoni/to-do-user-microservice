package com.bogdan.todouser.service;

import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.dto.UserDto;

import java.util.Optional;


public interface TaskService {

    Optional<UserDto> findTasksByUserId(Long userId);

    UserDto createTask(TaskDto taskDto, Long id);

    UserDto updateTask(TaskDto taskDto, Long userId);

    Boolean deleteTask(Long id);
}
