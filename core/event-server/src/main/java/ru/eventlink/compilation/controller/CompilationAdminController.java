package ru.eventlink.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.compilation.service.CompilationAdminService;
import ru.eventlink.dto.compilation.CompilationDto;
import ru.eventlink.dto.compilation.NewCompilationDto;
import ru.eventlink.dto.compilation.UpdateCompilationRequest;

@RestController
@RequestMapping("/api/v1/admin/compilations")
@Slf4j
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationAdminService compilationAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Add compilation: {}", compilationDto);
        return compilationAdminService.addCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest request) {
        log.info("Update compilation: {}", request);
        return compilationAdminService.updateCompilation(compId, request);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Delete compilation: {}", compId);
        compilationAdminService.deleteCompilation(compId);
    }
}
