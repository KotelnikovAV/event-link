package ru.eventlink.category.service;

import ru.eventlink.category.model.Category;

import java.util.List;

public interface CategoryPublicService {
    List<Category> getAllCategories(int page, int size);

    Category getCategory(long catId);
}
