package ru.eventlink.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.eventlink.compilation.mapper.CompilationMapper;
import ru.eventlink.compilation.model.Compilation;
import ru.eventlink.compilation.repository.CompilationRepository;
import ru.eventlink.dto.compilation.CompilationDto;
import ru.eventlink.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int page, int size) {
        log.info("The beginning of the process of finding a all compilations");
        PageRequest pageRequest = PageRequest.of(page, size);
        List<CompilationDto> compilationsDto;
        List<Long> compilationsId = compilationRepository.findIds(pageRequest).getContent();

        if (pinned == null) {
            compilationsDto = compilationMapper.listCompilationToListCompilationDto(compilationRepository
                    .findAllByIdIn(compilationsId));
        } else if (pinned) {
            compilationsDto = compilationMapper.listCompilationToListCompilationDto(
                    compilationRepository.findAllByPinnedTrueAndIdIn(compilationsId));
        } else {
            compilationsDto = compilationMapper.listCompilationToListCompilationDto(
                    compilationRepository.findAllByPinnedFalseAndIdIn(compilationsId));
        }

        log.info("The all compilations has been found");
        return compilationsDto;
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        log.info("The beginning of the process of finding a all compilations by id");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id " + compId + " not found"));
        log.info("The all compilations by id has been found");
        return compilationMapper.compilationToCompilationDto(compilation);
    }
}
