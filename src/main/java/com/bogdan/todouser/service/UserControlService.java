package com.bogdan.todouser.service;

import com.bogdan.todouser.config.TaskProxy;
import com.bogdan.todouser.dao.UserDao;
import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.dto.UserResponseDto;
import com.bogdan.todouser.entity.UserEntity;
import com.bogdan.todouser.enums.ErrorsEnum;
import com.bogdan.todouser.exception.CustomException;
import com.bogdan.todouser.exception.RestResponse;
import com.bogdan.todouser.properties.AuthorizationProperties;
import com.bogdan.todouser.security.AuthorizationService;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserControlService {

    private Logger logger = LoggerFactory.getLogger(UserControlService.class);

    @Autowired
    private UserDao userDao;

    private TaskProxy proxy;

    @Autowired
    private AuthorizationService authorizationService;

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
    public ResponseEntity<String> findByUsername(String username) {
        UserEntity user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No user with id " + username + " found"));

        UserResponseDto userResponseDto = mapToUserEntityResponseDto(user);

        return RestResponse.createSuccessResponse(new JSONObject(userResponseDto));
    }

    public ResponseEntity<String> register(UserDto userDto) {
        Optional<UserEntity> existingUser = userDao.findByUsername(userDto.getUsername());

        if(existingUser.isPresent()) {
            throw new CustomException(ErrorsEnum.USER_EXISTS);
        }

        UserEntity user = mapToUserEntity(userDto);

        return RestResponse.createSuccessResponse(new JSONObject(
                mapToUserEntityResponseDto(userDao.createEntity(user))));
    }

    public ResponseEntity<String> login(UserDto userDto) {

        UserEntity user = userDao.findByUsername(userDto.getUsername())
                .orElseThrow(() -> new CustomException(ErrorsEnum.USER_NOT_FOUND));

        if(!StringUtils.equals(userDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorsEnum.LOGIN_WRONG_CREDENTIALS);
        }

        String jwtToken = authorizationService.generateJwtToken(user.getId(), user.getUsername());

        UserResponseDto userResponseDto = mapToUserEntityResponseDto(user);
        userResponseDto.setJwtToken(jwtToken);
        logger.info("User: " + userDto.getUsername() + " logged in");

        return RestResponse.createSuccessResponse(new JSONObject(userResponseDto));
    }

    public ResponseEntity<String> update(UserDto userDto) {

        UserEntity user = userDao.findById(userDto.getId())
                .orElseThrow(() -> new CustomException(ErrorsEnum.USER_NOT_FOUND));

        mapToUserEntity(userDto, user);

        UserResponseDto responseDto = mapToUserEntityResponseDto(user);

        return RestResponse.createSuccessResponse(new JSONObject(responseDto));
    }

    public void deleteById(Long id) {

        userDao.findById(id).orElseThrow(() ->
                new CustomException(ErrorsEnum.USER_NOT_FOUND)
        );

        userDao.deleteEntityById(id);
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
