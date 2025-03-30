package ru.eventlink.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.eventlink.compilation.model.Compilation;

import java.util.List;
import java.util.Optional;


public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @EntityGraph(attributePaths = "events")
    List<Compilation> findAllByPinnedTrueAndIdIn(List<Long> ids);

    @EntityGraph(attributePaths = "events")
    List<Compilation> findAllByPinnedFalseAndIdIn(List<Long> ids);

    @EntityGraph(attributePaths = "events")
    List<Compilation> findAllByIdIn(List<Long> ids);

    @Override
    @EntityGraph(attributePaths = "events")
    Optional<Compilation> findById(Long id);

    @Query("SELECT c.id " +
            "FROM Compilation c")
    Page<Long> findIds(Pageable pageable);
}
