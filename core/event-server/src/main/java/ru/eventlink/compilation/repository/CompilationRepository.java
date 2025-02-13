package ru.eventlink.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.eventlink.compilation.model.Compilation;


public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Page<Compilation> findAllByPinnedTrue(Pageable pageable);

    Page<Compilation> findAllByPinnedFalse(Pageable pageable);
}
