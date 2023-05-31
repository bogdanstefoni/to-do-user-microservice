package com.bogdan.todouser.mapper;

import com.bogdan.todouser.domain.User;
import com.bogdan.todouser.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    User userDtoToUser(UserDto dto);

    UserDto userToUserDto(User user);
}
