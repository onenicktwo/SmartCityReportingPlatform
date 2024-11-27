package org.citywatcher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.citywatcher.model.User;
import org.citywatcher.service.FileStorageService;
import org.citywatcher.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
public class NicholasMorrowSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @Before
    public void setup() {
        testUser = new User();
        testUser.setId(0L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
    }

    @Test
    public void testRegisterUserWithImage() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("image", "profile.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes());
        MockMultipartFile userFile = new MockMultipartFile("user", "", "application/json", objectMapper.writeValueAsBytes(testUser));

        when(userService.registerUser(any(User.class), any())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/citywatcher/users/register")
                        .file(userFile)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));
    }

    @Test
    public void testGetUserById() throws Exception {
        when(userService.getUserById(0L)).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/citywatcher/users/0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/citywatcher/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testUser.getId()))
                .andExpect(jsonPath("$[0].username").value(testUser.getUsername()));
    }

    @Test
    public void testUpdateUserWithImage() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("image", "updated_profile.jpg", MediaType.IMAGE_JPEG_VALUE, "updated image data".getBytes());
        MockMultipartFile userFile = new MockMultipartFile("user", "", "application/json", objectMapper.writeValueAsBytes(testUser));

        when(userService.updateUser(Mockito.eq(0L), any(User.class), any())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/citywatcher/users/0")
                        .file(userFile)
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(userService.deleteUser(0L)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/citywatcher/users/0"))
                .andExpect(status().isNoContent());
    }
}