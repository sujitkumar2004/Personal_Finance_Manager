//package com.sujit.personal_Finance_Manager.service;
//
//import com.sujit.personal_Finance_Manager.entity.*;
//import com.sujit.personal_Finance_Manager.repository.CategoryRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//class CategoryServiceTest {
//
//    @Mock
//    private CategoryRepository categoryRepository;
//
//    @InjectMocks
//    private CategoryService categoryService;
//
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        user = User.builder().id(1L).username("test@example.com").build();
//    }
//
//    @Test
//    void findValidCategory_returns_category() {
//        Category category = Category.builder().id(1L).name("Food").type(CategoryType.EXPENSE).user(user).build();
//
//        when(categoryRepository.findByNameAndUser("Food", user)).thenReturn(Optional.of(category));
//
//        Category result = categoryService.findValidCategory(user, "Food");
//        assertThat(result.getName()).isEqualTo("Food");
//        assertThat(result.getType()).isEqualTo(CategoryType.EXPENSE);
//    }
//}
//
package com.sujit.personal_Finance_Manager.service;

import com.sujit.personal_Finance_Manager.entity.*;
import com.sujit.personal_Finance_Manager.exception.BadRequestException;
import com.sujit.personal_Finance_Manager.exception.ConflictException;
import com.sujit.personal_Finance_Manager.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder().id(1L).username("testuser").build();
    }

    @Test
    void initializeDefaultsForUser_ShouldSaveDefaults_WhenUserHasNoCategories() {
        when(categoryRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        categoryService.initializeDefaultsForUser(testUser);

        // Should save all default income + expense categories
        verify(categoryRepository, times(1 + 6)).save(any(Category.class));
    }

    @Test
    void initializeDefaultsForUser_ShouldNotSaveDefaults_WhenUserHasCategories() {
        List<Category> existing = List.of(Category.builder().name("Food").build());
        when(categoryRepository.findByUser(testUser)).thenReturn(existing);

        categoryService.initializeDefaultsForUser(testUser);

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getAll_ShouldReturnAllCategories() {
        List<Category> categories = List.of(
                Category.builder().name("Food").user(testUser).build()
        );
        when(categoryRepository.findByUser(testUser)).thenReturn(categories);

        List<Category> result = categoryService.getAll(testUser);

        assertThat(result).containsExactlyElementsOf(categories);
    }

    @Test
    void createCustom_ShouldCreate_WhenNameNotExists() {
        String name = "Investment";
        when(categoryRepository.existsByNameAndUser(name, testUser)).thenReturn(false);

        Category expected = Category.builder()
                .name(name)
                .type(CategoryType.EXPENSE)
                .isCustom(true)
                .user(testUser)
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(expected);

        Category created = categoryService.createCustom(testUser, name, CategoryType.EXPENSE);

        assertThat(created.getName()).isEqualTo(name);
        assertThat(created.getIsCustom()).isTrue();
    }

    @Test
    void createCustom_ShouldThrowConflictException_WhenNameExists() {
        String name = "Food";
        when(categoryRepository.existsByNameAndUser(name, testUser)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCustom(testUser, name, CategoryType.EXPENSE))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void deleteCustom_ShouldDelete_WhenCustomCategory() {
        String name = "Gift";
        Category custom = Category.builder().name(name).isCustom(true).user(testUser).build();

        when(categoryRepository.findByNameAndUser(name, testUser)).thenReturn(Optional.of(custom));

        categoryService.deleteCustom(testUser, name);

        verify(categoryRepository).delete(custom);
    }

    @Test
    void deleteCustom_ShouldThrow_WhenDefaultCategory() {
        String name = "Salary";
        Category def = Category.builder().name(name).isCustom(false).user(testUser).build();

        when(categoryRepository.findByNameAndUser(name, testUser)).thenReturn(Optional.of(def));

        assertThatThrownBy(() -> categoryService.deleteCustom(testUser, name))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot be deleted");
    }

    @Test
    void deleteCustom_ShouldThrow_WhenCategoryNotFound() {
        when(categoryRepository.findByNameAndUser("Unknown", testUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCustom(testUser, "Unknown"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void findValidCategory_ShouldReturn_WhenExists() {
        Category cat = Category.builder().name("Rent").user(testUser).build();
        when(categoryRepository.findByNameAndUser("Rent", testUser)).thenReturn(Optional.of(cat));

        Category result = categoryService.findValidCategory(testUser, "Rent");

        assertThat(result).isEqualTo(cat);
    }

    @Test
    void findValidCategory_ShouldThrow_WhenNotExists() {
        when(categoryRepository.findByNameAndUser("Ghost", testUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findValidCategory(testUser, "Ghost"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("does not exist");
    }
}
