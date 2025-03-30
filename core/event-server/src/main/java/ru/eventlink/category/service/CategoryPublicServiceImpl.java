package ru.eventlink.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eventlink.category.model.Category;
import ru.eventlink.category.repository.CategoryRepository;
import ru.eventlink.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicServiceImpl implements CategoryPublicService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories(int page, int size) {
        log.info("The beginning of the process of finding a categories");
        PageRequest pageRequest = PageRequest.of(page, size);
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
