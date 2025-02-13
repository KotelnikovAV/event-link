package ru.eventlink.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.category.mapper.CategoryMapper;
import ru.eventlink.category.service.CategoryService;
import ru.eventlink.dto.category.CategoryDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0")
                                              @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10")
                                              @Positive Integer size) {
        log.info("getAllCategories: from={}, size={}", from, size);
        return categoryMapper.listCategoryToListCategoryDto(categoryService.getAllCategories(from, size));
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        log.info("getCategory: catId={}", catId);
        return categoryMapper.categoryToCategoryDto(categoryService.getCategory(catId));
    }

}
