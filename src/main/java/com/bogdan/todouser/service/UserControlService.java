package com.bogdan.todouser.service;

import com.bogdan.todouser.config.TaskProxy;
import com.bogdan.todouser.dao.UserDao;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.dto.UserResponseDto;
import com.bogdan.todouser.entity.UserEntity;
import com.bogdan.todouser.exception.RestResponse;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserControlService {

    private UserDao userDao;

    private TaskProxy proxy;

    public ResponseEntity<String> getAllUsers() {
        List<UserEntity> users = userDao.findAllUsers();
        List<UserResponseDto> userResponseDto = new ArrayList<>();

        users.forEach(u -> {
            UserResponseDto responseDto = mapToUserEntityResponseDto(u);
            userResponseDto.add(responseDto);
        });

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("users", userResponseDto);

        return RestResponse.createSuccessResponse(jsonObject);
    }

    public ResponseEntity<String> findUserById(Long id) {
        UserEntity user = userDao.findById(id)
                .orElseThrow(() -> new RuntimeException("No user with id " + id + " found"));

        UserResponseDto userResponseDto = mapToUserEntityResponseDto(user);

        return RestResponse.createSuccessResponse(new JSONObject(userResponseDto));
    }

    public ResponseEntity<String> registerUser(UserDto userDto) {
        Optional<UserEntity> existingUser = userDao.findById(userDto.getId());

        if(existingUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        UserEntity user = mapToUserEntity(userDto);

        return RestResponse.createSuccessResponse(new JSONObject(
                mapToUserEntityResponseDto(userDao.createEntity(user))));
    }



    private UserEntity mapToUserEntity(UserDto userDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(userDto, UserEntity.class);
    }

    private void mapToUserEntity(UserDto userDto, UserEntity userEntity) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.addMappings(new PropertyMap<UserDto, UserEntity>() {
            @Override
            protected void configure() {
//                skip(destination.getPassword());
            }
        });
        mapper.map(userDto, userEntity);
    }

    private UserResponseDto mapToUserEntityResponseDto(UserEntity userEntity) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(userEntity, UserResponseDto.class);
    }
}
