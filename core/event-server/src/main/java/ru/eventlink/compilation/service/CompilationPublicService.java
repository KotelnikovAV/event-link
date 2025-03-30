package ru.eventlink.compilation.service;

import ru.eventlink.dto.compilation.CompilationDto;

import java.util.List;

public interface CompilationPublicService {
    List<CompilationDto> getAllCompilations(Boolean pinned, int page, int size);

    CompilationDto getCompilationById(long compId);
}
