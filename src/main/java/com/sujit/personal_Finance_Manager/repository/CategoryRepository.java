package com.sujit.personal_Finance_Manager.repository;

import com.sujit.personal_Finance_Manager.entity.Category;
import com.sujit.personal_Finance_Manager.entity.User;
import com.sujit.personal_Finance_Manager.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndUser(String name, User user);
    List<Category> findByUser(User user);
    List<Category> findByTypeAndUser(CategoryType type, User user);
    boolean existsByNameAndUser(String name, User user);
}
