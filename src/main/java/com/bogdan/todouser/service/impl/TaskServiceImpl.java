package com.bogdan.todouser.service.impl;

import com.bogdan.todouser.config.TaskProxy;
import com.bogdan.todouser.dto.TaskDto;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.enums.ErrorsEnum;
import com.bogdan.todouser.exception.CustomException;
import com.bogdan.todouser.service.TaskService;
import com.bogdan.todouser.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Optional<UserDto> findTasksByUserId(Long userId) {
        Optional<UserDto> user = userService.findUserById(userId);

            List<TaskDto> tasksResponse = proxy.findTasksByUserId(userId);

            user.get().setTasks(tasksResponse);


        return user;
    }

    @Override
    public UserDto createTask(TaskDto taskDto, Long id) {
        Optional<UserDto> user = userService.findUserById(id);
        UserDto userDto = user.orElseThrow(() -> new CustomException(ErrorsEnum.USER_NOT_FOUND));
        taskDto.setUserId(id);


            List<TaskDto> existingTasks = proxy.findTasksByUserId(id);

            existingTasks.forEach(t -> {
                if (t.getTitle().equals(taskDto.getTitle())) {
                    throw new CustomException(ErrorsEnum.TASK_EXISTS);
                }
            });


            List<TaskDto> taskDtos = new ArrayList<>();
            taskDtos.add(taskDto);
            userDto.setTasks(taskDtos);

            proxy.createTask(taskDto, id);
            logger.info("Task created: {}", userDto.getTasks());



        return userDto;

    }

    @Override
    public UserDto updateTask(TaskDto taskDto, Long id) {
        Optional<UserDto> user = userService.findUserById(id);

        UserDto userDto = user.get();
        if (userDto.isNotLocked()) {
            List<TaskDto> taskDtos = proxy.findTasksByUserId(id);
            taskDtos.stream().filter(t -> t.getTitle().equals(taskDto.getTitle()))
                    .forEach(t -> {
                        t.setTitle(taskDto.getTitle());
                        t.setTaskDescription(taskDto.getTaskDescription());
                    });

            userDto.setTasks(taskDtos);

            proxy.updateTask(taskDto, id);

        }

        return userDto;
    }

    @Override
    public Boolean deleteTask(Long id) {
        if (proxy.findById(id) != null) {
            proxy.deleteTask(id);
            return true;
        }
        return false;
    }
}
