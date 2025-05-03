package ru.eventlink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eventlink.client.GrpcClient;
import ru.eventlink.client.RestClient;
import ru.eventlink.dto.event.EventFullDto;
import ru.eventlink.dto.requests.ParticipationRequestDto;
import ru.eventlink.enums.State;
import ru.eventlink.enums.Status;
import ru.eventlink.exception.IntegrityViolationException;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.mapper.RequestMapper;
import ru.eventlink.model.Request;
import ru.eventlink.repository.RequestsRepository;
import ru.eventlink.stats.proto.ActionTypeProto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestsRepository requestsRepository;
    private final RequestMapper requestMapper;
    private final RestClient restClient;
    private final GrpcClient grpcClient;

    @Override
    public List<ParticipationRequestDto> findAllRequestsByUserId(long userId) {
        log.info("The beginning of the process of finding all requests");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        List<Request> requests = requestsRepository.findAllByRequesterId(userId);
        log.info("The all requests has been found");
        return requestMapper.listRequestToListParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        log.info("The beginning of the process of creating a request");

        requestsRepository.findByEventIdAndRequesterId(eventId, userId).ifPresent(
                r -> {
                    throw new IntegrityViolationException(
                            "Request with userId " + userId + " eventId " + eventId + " exists");
                });

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        if (restClient.findExistEventByEventIdAndInitiatorId(eventId, userId)) {
            throw new IntegrityViolationException("UserId " + userId + " initiates  eventId " + eventId);
        }

        EventFullDto event = restClient.findEventById(eventId);

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IntegrityViolationException("Event with id = " + eventId + " is not published");
        }

        List<Request> confirmedRequests = requestsRepository.findAllByStatusAndEventId(Status.CONFIRMED, eventId);

        if ((!event.getParticipantLimit().equals(0L))
                && (event.getParticipantLimit() == confirmedRequests.size())) {
            throw new IntegrityViolationException("Request limit exceeded");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequesterId(userId);
        request.setEventId(eventId);

        if ((event.getParticipantLimit().equals(0L)) || (!event.getRequestModeration())) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }

        request = requestsRepository.save(request);

        grpcClient.collectUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER);

        log.info("The request has been created");
        return requestMapper.requestToParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        log.info("The beginning of the process of canceling a request");

        if (!restClient.getUserExists(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Request request = requestsRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id = " + requestId + " not found"));

        request.setStatus(Status.CANCELED);
        log.info("The request has been canceled");
        return requestMapper.requestToParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsByRequestsId(Set<Long> requestsId) {
        log.info("The beginning of the process of finding all requests by requestsId");

        List<Request> requests = requestsRepository.findByIdIn(requestsId);

        log.info("The all requests has been found");
        return requestMapper.listRequestToListParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> updateRequest(Set<Long> requestsId, Status status) {
        log.info("The beginning of the process of updating a request");

        List<Request> requests = requestsRepository.findByIdIn(requestsId);
        requests.forEach(request -> request.setStatus(status));

        log.info("The request has been updated");
        return requestMapper.listRequestToListParticipationRequestDto(requests);
    }

    @Override
    public boolean findExistRequests(Long eventId, Long userId, Status status) {
        log.info("The beginning of the process of finding exist requests");
        return requestsRepository.existsByEventIdAndRequesterIdAndStatus(eventId, userId, status);
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestsByEventIdAndStatus(Long eventId, Status status) {
        log.info("The beginning of the process of finding all requests by eventId");

        List<Request> requests;

        if (status == null) {
            requests = requestsRepository.findByEventId(eventId);
        } else {
            requests = requestsRepository.findAllByStatusAndEventId(status, eventId);
        }

        log.info("The all requests has been found");
        return requestMapper.listRequestToListParticipationRequestDto(requests);
    }
}
