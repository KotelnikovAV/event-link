package ru.eventlink.category.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.eventlink.category.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findCategoriesByNameContainingIgnoreCase(String name);

    boolean existsByNameContainingIgnoreCase(@NotBlank String name);
}
