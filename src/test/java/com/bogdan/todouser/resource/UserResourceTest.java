package com.bogdan.todouser.resource;

import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.service.UserService;
import com.bogdan.todouser.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserResourceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Test
    void testRegisterWithSameUsername() throws Exception {
        UserDto userDto = UserDto.builder()
                .username("john")
                .email("john@email.com")
                .password("1234")
                .build();


//        given(userService.register(any(UserDto.class))).willReturn(userServiceImpl.getUsers().get(0));

        MvcResult result = mockMvc.perform(post(UserResource.REGISTER)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated()).andReturn();

        MvcResult result1 = mockMvc.perform(post(UserResource.REGISTER)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        System.out.println(result1.getResponse().getContentAsString());

//        given(userService.register(any(UserDto.class))).willReturn(userServiceImpl.getUsers().get(1));
//
//        MvcResult result1 = mockMvc.perform(post(UserResource.REGISTER)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userDto1)))
//                .andExpect(status().isCreated()).andReturn();
//
//        System.out.println(result1.getResponse().getContentAsString());
    }
}