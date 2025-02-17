package ru.eventlink.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.compilation.service.CompilationService;
import ru.eventlink.dto.compilation.CompilationDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("getAllCompilations with pinned={}, page={}, size={}", pinned, page, size);
        return compilationService.getAllCompilations(pinned, page, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("getCompilationById {}", compId);
        return compilationService.getCompilationById(compId);
    }
}
