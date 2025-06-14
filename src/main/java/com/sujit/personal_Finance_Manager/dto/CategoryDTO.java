package com.sujit.personal_Finance_Manager.dto;

import com.sujit.personal_Finance_Manager.entity.CategoryType;
import lombok.Data;

@Data
public class CategoryDTO {
    private String name;
    private CategoryType type;
    private Boolean isCustom;
}
