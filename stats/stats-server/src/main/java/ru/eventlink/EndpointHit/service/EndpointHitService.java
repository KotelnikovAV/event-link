package ru.eventlink.EndpointHit.service;

import ru.eventlink.EndpointHit.model.EndpointHit;
import ru.eventlink.ViewStats.model.ViewStats;

import java.util.List;

public interface EndpointHitService {
    void save(EndpointHit endpointHit);

    List<ViewStats> findByParams(String start, String end, List<String> uris, boolean unique);
}
