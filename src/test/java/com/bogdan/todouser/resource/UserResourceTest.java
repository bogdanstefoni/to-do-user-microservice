package com.bogdan.todouser.resource;

import com.bogdan.todouser.dto.UserDto;
import com.bogdan.todouser.service.UserService;
import com.bogdan.todouser.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
                .username("bob")
                .password("1223")
                .email("bob@Email")
                .build();
        userServiceImpl.register(userDto);
    }

    @Test
    void testDeleteUserByIdWithoutAccessRights() {

    }

    @Test
    void testGetUserById() throws Exception {
        UserDto testUserDtp = userService.getUsers().get(0);


//        given(userService.findUserById(testUserDtp.getId())).willReturn(Optional.of(testUserDtp));

        mockMvc.perform(get(UserResource.USER_ID_PATH, testUserDtp.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testUserDtp.getId().intValue())))
                .andExpect(jsonPath("$.username", is(testUserDtp.getUsername())));
    }

    @Test
    void testListUsers() throws Exception {

//        given(userService.getUsers()).willReturn(userServiceImpl.getUsers());

        mockMvc.perform(get(UserResource.USER_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(1)));

    }

    @Test
    void testRegisterWithSameUsername() throws Exception {
        UserDto userDto = UserDto.builder()
                .username("john")
                .email("john@email.com")
                .password("1234")
                .build();

        MvcResult result = mockMvc.perform(post(UserResource.REGISTER)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated()).andReturn();

        MvcResult result1 = mockMvc.perform(post(UserResource.REGISTER)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotAcceptable()).andReturn();

        System.out.println(result.getResponse().getContentAsString());
        System.out.println(result1.getResponse().getContentAsString());

    }
}