package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.entity.Category;
import com.sujit.personal_Finance_Manager.entity.CategoryType;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.exception.BadRequestException;
import com.sujit.personal_Finance_Manager.exception.ConflictException;
import com.sujit.personal_Finance_Manager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private static final List<String> DEFAULT_INCOME = List.of("Salary");
    private static final List<String> DEFAULT_EXPENSE = Arrays.asList("Food", "Rent", "Transportation", "Entertainment", "Healthcare", "Utilities");

    // Ensure default categories are in DB for a user
    public void initializeDefaultsForUser(User user) {
        if (categoryRepository.findByUser(user).isEmpty()) {
            DEFAULT_INCOME.forEach(name -> saveDefault(user, name, CategoryType.INCOME));
            DEFAULT_EXPENSE.forEach(name -> saveDefault(user, name, CategoryType.EXPENSE));
        }
    }

    private void saveDefault(User user, String name, CategoryType type) {
        Category c = Category.builder()
                .name(name)
                .type(type)
                .isCustom(false)
                .user(user)
                .build();
        categoryRepository.save(c);
    }

    public List<Category> getAll(User user) {
        initializeDefaultsForUser(user);
        return categoryRepository.findByUser(user);
    }

    public Category createCustom(User user, String name, CategoryType type) {
        if (categoryRepository.existsByNameAndUser(name, user)) {
            throw new ConflictException("Category name already exists for this user.");
        }
        Category c = Category.builder()
                .name(name)
                .type(type)
                .isCustom(true)
                .user(user)
                .build();
        return categoryRepository.save(c);
    }

//    public void deleteCustom(User user, String name) {
//        Category cat = categoryRepository.findByNameAndUser(name, user)
//                .orElseThrow(() -> new BadRequestException("Category not found."));
//        if (!cat.getIsCustom()) {
//            throw new BadRequestException("Default categories cannot be deleted.");
//        }
//        categoryRepository.delete(cat);
//    }

    public void deleteCustom(User user, String name) {
        Category cat = categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new BadRequestException("Category not found."));

        if (!cat.getIsCustom()) {
            throw new BadRequestException("Default categories cannot be deleted.");
        }

        try {
            categoryRepository.delete(cat); // May throw due to FK constraint
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Category is in use and cannot be deleted.");
        }
    }


    public Category findValidCategory(User user, String name) {
        return categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new BadRequestException("Category does not exist or is deleted."));
    }

    //for create category if not default
    public Category findOrCreateCategory(User user, String name, CategoryType type) {
        return categoryRepository.findByNameAndUser(name, user)
                .orElseGet(() -> {
                    Category c = Category.builder()
                            .name(name)
                            .type(type)
                            .isCustom(true)
                            .user(user)
                            .build();
                    return categoryRepository.save(c);
                });
    }

}
