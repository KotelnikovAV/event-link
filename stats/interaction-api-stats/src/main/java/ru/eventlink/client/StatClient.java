package ru.eventlink.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.eventlink.dto.EndpointHitDto;
import ru.eventlink.dto.ViewStatsDto;
import ru.eventlink.fallback.StatFallback;

import java.util.List;

@FeignClient(name = "stats-server", fallback = StatFallback.class)
public interface StatClient {
    @PostMapping("hit")
    void save(@Valid @RequestBody EndpointHitDto endpointHitDto);

    @GetMapping("stats")
    List<ViewStatsDto> findByParams(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false) boolean unique);
}
