package ru.eventlink.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.category.mapper.CategoryMapper;
import ru.eventlink.category.model.Category;
import ru.eventlink.category.service.CategoryAdminService;
import ru.eventlink.dto.category.CategoryDto;
import ru.eventlink.dto.category.NewCategoryDto;
import ru.eventlink.dto.category.UpdateCategoryDto;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {
    private final CategoryAdminService categoryAdminService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Add category: {}", newCategoryDto);
        Category category = categoryMapper.newCategoryDtoToCategory(newCategoryDto);
        return categoryMapper.categoryToCategoryDto(categoryAdminService.addCategory(category));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long catId) {
        log.info("Delete category: {}", catId);
        categoryAdminService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable long catId, @RequestBody @Valid UpdateCategoryDto dto) {
        log.info("Update category: {}", dto);
        return categoryMapper.categoryToCategoryDto(
                categoryAdminService.updateCategory(catId, categoryMapper.updateCategoryDtoToCategory(dto)));
    }
}
