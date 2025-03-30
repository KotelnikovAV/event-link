package ru.eventlink.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.eventlink.compilation.mapper.CompilationMapper;
import ru.eventlink.compilation.model.Compilation;
import ru.eventlink.compilation.repository.CompilationRepository;
import ru.eventlink.dto.compilation.CompilationDto;
import ru.eventlink.dto.compilation.NewCompilationDto;
import ru.eventlink.dto.compilation.UpdateCompilationRequest;
import ru.eventlink.event.repository.EventRepository;
import ru.eventlink.exception.NotFoundException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        log.info("The beginning of the process of creating a compilation");
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(compilationDto);
        List<Long> ids = compilationDto.getEvents();

        if (!CollectionUtils.isEmpty(ids)) {
            compilation.setEvents(eventRepository.findAllByIdIn(ids));
        } else {
            compilation.setEvents(Collections.emptyList());
        }

        Compilation createdCompilation = compilationRepository.save(compilation);
        log.info("The compilation has been created");
        return compilationMapper.compilationToCompilationDto(createdCompilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest request) {
        log.info("The beginning of the process of updating a compilation");

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id " + compId + " not found"));

        if (!CollectionUtils.isEmpty(request.getEvents())) {
            compilation.setEvents(eventRepository.findAllByIdIn(request.getEvents()));
        }

        if (request.getPinned() != null) compilation.setPinned(request.getPinned());

        if (request.getTitle() != null) compilation.setTitle(request.getTitle());

        log.info("The compilation has been updated");
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(long compId) {
        log.info("The beginning of the process of deleting a compilation");

        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
        } else {
            throw new NotFoundException("Compilation with id " + compId + " not found");
        }

        log.info("The compilation has been deleted");
    }
}
