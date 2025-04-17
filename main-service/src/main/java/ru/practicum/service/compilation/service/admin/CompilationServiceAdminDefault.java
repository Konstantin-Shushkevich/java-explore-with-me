package ru.practicum.service.compilation.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.dto.NewCompilationDto;
import ru.practicum.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.compilation.repository.CompilationRepository;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.event.service.admin.EventServiceAdminDefault;
import ru.practicum.service.exception.CompilationIsNotInRepositoryException;
import ru.practicum.service.exception.EventIsNotInRepositoryException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static ru.practicum.service.compilation.dto.mapper.CompilationMapper.toCompilation;
import static ru.practicum.service.compilation.dto.mapper.CompilationMapper.toCompilationDto;

@Service
@RequiredArgsConstructor
public class CompilationServiceAdminDefault implements CompilationServiceAdmin {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventServiceAdminDefault eventService;

    @Override
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>();

        if (!Objects.isNull(newCompilationDto.getEvents()) && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findByIdIn(newCompilationDto.getEvents());
            int countOfSkipped = newCompilationDto.getEvents().size() - events.size();

            if (countOfSkipped != 0) {
                throw new EventIsNotInRepositoryException("From the provided list of events " + countOfSkipped
                        + "events are not found");
            }
        }

        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }

        return toCompilationDto(compilationRepository.save(
                toCompilation(newCompilationDto, events)), eventService.toEventShortDtoList(events));
    }

    @Override
    public void deleteById(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new CompilationIsNotInRepositoryException("Compilation with id: " + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateById(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationFromBd = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationIsNotInRepositoryException("Compilation with id: " + compId +
                        " was not found"));
        Set<Event> events;
        if (Objects.isNull(updateCompilationRequest.getEvents())) {
            events = compilationFromBd.getEvents();
        } else if (!updateCompilationRequest.getEvents().isEmpty()) {
            events = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
            int countOfSkipped = updateCompilationRequest.getEvents().size() - events.size();
            if (countOfSkipped != 0) {
                throw new EventIsNotInRepositoryException("From the provided list of events " + countOfSkipped
                        + "events are not found");
            }
        } else {
            events = Collections.emptySet();
        }

        return toCompilationDto(compilationRepository.save(toCompilation(
                updateCompilationRequest, compilationFromBd, events)), eventService.toEventShortDtoList(events));
    }
}

