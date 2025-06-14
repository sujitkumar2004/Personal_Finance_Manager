package com.sujit.personal_Finance_Manager.controller;

import com.sujit.personal_Finance_Manager.dto.CategoryDTO;
import com.sujit.personal_Finance_Manager.entity.Category;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.service.CategoryService;
import com.sujit.personal_Finance_Manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public List<CategoryDTO> getCategories(Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        return categoryService.getAll(user).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(Principal principal, @RequestBody CategoryDTO dto) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        Category category = categoryService.createCustom(user, dto.getName(), dto.getType());
        return toDTO(category);
    }

    @DeleteMapping("/{name}")
    public void deleteCategory(Principal principal, @PathVariable String name) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        categoryService.deleteCustom(user, name);
    }

    private CategoryDTO toDTO(Category c) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(c.getName());
        dto.setType(c.getType());
        dto.setIsCustom(c.getIsCustom());
        return dto;
    }
}
