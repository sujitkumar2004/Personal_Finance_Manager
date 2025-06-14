package com.sujit.personal_Finance_Manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujit.personal_Finance_Manager.dto.CategoryDTO;
import com.sujit.personal_Finance_Manager.entity.Category;
import com.sujit.personal_Finance_Manager.entity.CategoryType;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.service.CategoryService;
import com.sujit.personal_Finance_Manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        return user;
    }

    private Category getMockCategory() {
        Category category = new Category();
        category.setName("Health");
        category.setType(CategoryType.EXPENSE);
        category.setIsCustom(true);
        return category;
    }

    @WithMockUser(username = "testuser")
    @Test
    void testGetCategories() throws Exception {
        User user = getMockUser();
        Category category = getMockCategory();

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.when(categoryService.getAll(user)).thenReturn(List.of(category));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Health"))
                .andExpect(jsonPath("$[0].type").value("EXPENSE"))
                .andExpect(jsonPath("$[0].isCustom").value(true));
    }

    @WithMockUser(username = "testuser")
    @Test
    void testCreateCategory() throws Exception {
        User user = getMockUser();
        Category category = getMockCategory();

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Health");
        dto.setType(CategoryType.EXPENSE);

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.when(categoryService.createCustom(user, "Health", CategoryType.EXPENSE)).thenReturn(category);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Health"))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.isCustom").value(true));
    }

    @WithMockUser(username = "testuser")
    @Test
    void testDeleteCategory() throws Exception {
        User user = getMockUser();

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        Mockito.doNothing().when(categoryService).deleteCustom(user, "Health");

        mockMvc.perform(delete("/api/categories/Health"))
                .andExpect(status().isOk());
    }
}
