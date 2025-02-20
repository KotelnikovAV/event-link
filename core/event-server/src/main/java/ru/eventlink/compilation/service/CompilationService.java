package ru.eventlink.compilation.service;

import ru.eventlink.dto.compilation.CompilationDto;
import ru.eventlink.dto.compilation.NewCompilationDto;
import ru.eventlink.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest request);

    void deleteCompilation(long compId);

    List<CompilationDto> getAllCompilations(Boolean pinned, int page, int size);

    CompilationDto getCompilationById(long compId);
}
