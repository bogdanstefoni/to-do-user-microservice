package com.bogdan.todouser.service.impl;

import com.bogdan.todouser.config.TaskProxy;
import com.bogdan.todouser.domain.User;
import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.dto.UserResponseDto;
import com.bogdan.todouser.exception.UserNotFoundException;
import com.bogdan.todouser.service.TaskService;
import com.bogdan.todouser.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Service
public class TaskServiceImpl implements TaskService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserService userService;
    private final TaskProxy proxy;

    public TaskServiceImpl(UserService userService, TaskProxy proxy) {
        this.userService = userService;
        this.proxy = proxy;
    }

    @Override
    public ResponseEntity<UserResponseDto> findTasksByUserId(long userId) throws UserNotFoundException {
        User user = userService.findUserById(userId);
        UserResponseDto userResponseDto = mapToUserResponseDto(user);
        if (user.isNotLocked()) {
            List<TaskDto> tasksResponse = proxy.findTasksByUserId(userId);

            userResponseDto.setTasks(tasksResponse);

            return new ResponseEntity<>(userResponseDto, OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


    }

    @Override
    public ResponseEntity<UserResponseDto> createTask(TaskDto taskDto, long id) throws UserNotFoundException {
        User user = userService.findUserById(id);
        taskDto.setUserId(id);
        if (user.isNotLocked()) {

            UserResponseDto responseDto = mapToUserResponseDto(user);
            List<TaskDto> taskDtos = new ArrayList<>();
            taskDtos.add(taskDto);
            responseDto.setTasks(taskDtos);

            proxy.createTask(taskDto, id);
            logger.info("Task created: {}",responseDto.getTasks());

            return new ResponseEntity<>(responseDto, OK);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @Override
    public ResponseEntity<List<TaskDto>> getAllTasks() {

        return proxy.getAllTasks();
    }

    private UserResponseDto mapToUserResponseDto(User user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(user, UserResponseDto.class);
    }

}
