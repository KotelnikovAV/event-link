package ru.eventlink.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eventlink.category.model.Category;
import ru.eventlink.category.repository.CategoryRepository;
import ru.eventlink.event.repository.EventRepository;
import ru.eventlink.exception.IntegrityViolationException;
import ru.eventlink.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Category addCategory(Category category) {
        log.info("The beginning of the process of creating a category");
        categoryRepository.findCategoriesByNameContainingIgnoreCase(category.getName().toLowerCase()).ifPresent(c -> {
            throw new IntegrityViolationException("Category name " + category.getName() + " already exists");
        });
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

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories(int from, int size) {
        log.info("The beginning of the process of finding a categories");
        PageRequest pageRequest = PageRequest.of(from, size);
        Page<Category> pageCategories = categoryRepository.findAll(pageRequest);
        List<Category> categories;

        if (pageCategories.hasContent()) {
            categories = pageCategories.getContent();
        } else {
            categories = Collections.emptyList();
        }

        log.info("The categories was found");
        return categories;
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategory(long catId) {
        log.info("The beginning of the process of finding a category");
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category with id=" + catId + " does not exist"));
        log.info("The category was found");
        return category;
    }
}
