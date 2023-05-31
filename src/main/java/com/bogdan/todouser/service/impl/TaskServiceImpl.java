package com.bogdan.todouser.service.impl;

import com.bogdan.todouser.config.TaskProxy;
import com.bogdan.todouser.domain.User;
import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.exception.UserNotFoundException;
import com.bogdan.todouser.service.TaskService;
import com.bogdan.todouser.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public UserDto findTasksByUserId(long userId) throws UserNotFoundException {
        UserDto user = userService.findUserById(userId);
        UserDto UserDto = mapToUserDto(user);
        if (user.isNotLocked()) {
            List<TaskDto> tasksResponse = proxy.findTasksByUserId(userId);

            UserDto.setTasks(tasksResponse);

        }

        return UserDto;


    }

    @Override
    public UserDto createTask(TaskDto taskDto, long id) throws UserNotFoundException {
        UserDto user = userService.findUserById(id);

        UserDto responseDto = mapToUserDto(user);
        taskDto.setUserId(id);
        if (user.isNotLocked()) {

            List<TaskDto> existingTasks = proxy.findTasksByUserId(id);

            existingTasks.forEach(t -> {
                if (t.getTitle().equals(taskDto.getTitle())) {
                    throw new RuntimeException("Task " + t.getTitle() + " already exists. Please create a new one");
                }
            });


            List<TaskDto> taskDtos = new ArrayList<>();
            taskDtos.add(taskDto);
            responseDto.setTasks(taskDtos);

            proxy.createTask(taskDto, id);
            logger.info("Task created: {}", responseDto.getTasks());


        }
        return responseDto;

    }

    @Override
    public UserDto updateTask(TaskDto taskDto, long id) throws UserNotFoundException {
        UserDto user = userService.findUserById(id);

        UserDto responseDto = mapToUserDto(user);

        if (user.isNotLocked()) {

            List<TaskDto> taskDtos = proxy.findTasksByUserId(id);
            taskDtos.stream().filter(t -> t.getTitle().equals(taskDto.getTitle()))
                            .forEach(t -> {
                                t.setTitle(taskDto.getTitle());
                                t.setTaskDescription(taskDto.getTaskDescription());
                            });

            responseDto.setTasks(taskDtos);

            proxy.updateTask(taskDto, id);

        }

        return responseDto;
    }

    private UserDto mapToUserDto(UserDto user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(user, UserDto.class);
    }

}
