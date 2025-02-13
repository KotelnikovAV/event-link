package ru.eventlink.fallback;

import org.springframework.stereotype.Component;
import ru.eventlink.client.StatClient;
import ru.eventlink.dto.EndpointHitDto;
import ru.eventlink.dto.ViewStatsDto;
import ru.eventlink.exception.ServerUnavailableException;

import java.util.List;

@Component
public class StatFallback implements StatClient {

    @Override
    public void save(EndpointHitDto endpointHitDto) {
        throw new ServerUnavailableException("Endpoint /hit method POST is unavailable");
    }

    @Override
    public List<ViewStatsDto> findByParams(String start, String end, List<String> uris, boolean unique) {
        throw new ServerUnavailableException("Endpoint /stats method GET is unavailable");
    }
}
