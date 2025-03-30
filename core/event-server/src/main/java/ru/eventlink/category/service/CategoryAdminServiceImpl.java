package ru.eventlink.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eventlink.category.model.Category;
import ru.eventlink.category.repository.CategoryRepository;
import ru.eventlink.event.repository.EventRepository;
import ru.eventlink.exception.IntegrityViolationException;
import ru.eventlink.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Category addCategory(Category category) {
        log.info("The beginning of the process of creating a category");

        boolean existsByName = categoryRepository.existsByNameContainingIgnoreCase(category.getName().toLowerCase());

        if (existsByName) {
            throw new IntegrityViolationException("Category name " + category.getName() + " already exists");
        }

        Category createCategory = categoryRepository.save(category);

        log.info("The category has been created");
        return createCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        log.info("The beginning of the process of deleting a category");

        categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category " + catId + " does not exist"));

        if (!eventRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new IntegrityViolationException("Category " + catId + " already exists");
        }

        categoryRepository.deleteById(catId);
        log.info("The category has been deleted");
    }

    @Override
    @Transactional
    public Category updateCategory(long catId, Category newCategory) {
        log.info("The beginning of the process of updating a category");

        Category updateCategory = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category with id=" + catId + " does not exist"));

        categoryRepository.findCategoriesByNameContainingIgnoreCase(
                newCategory.getName().toLowerCase()).ifPresent(c -> {
            if (c.getId() != catId) {
                throw new IntegrityViolationException("Category name " + newCategory.getName() + " already exists");
            }
        });

        updateCategory.setName(newCategory.getName());
        log.info("The category has been updated");
        return updateCategory;
    }
}
