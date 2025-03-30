package ru.eventlink.compilation.service;

import ru.eventlink.dto.compilation.CompilationDto;
import ru.eventlink.dto.compilation.NewCompilationDto;
import ru.eventlink.dto.compilation.UpdateCompilationRequest;

public interface CompilationAdminService {
    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest request);

    void deleteCompilation(long compId);
}
