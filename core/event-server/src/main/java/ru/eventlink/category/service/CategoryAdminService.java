package ru.eventlink.category.service;

import ru.eventlink.category.model.Category;

public interface CategoryAdminService {
    Category addCategory(Category category);

    void deleteCategory(long catId);

    Category updateCategory(long catId, Category category);
}
