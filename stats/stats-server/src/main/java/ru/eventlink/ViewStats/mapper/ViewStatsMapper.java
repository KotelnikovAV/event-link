package ru.eventlink.ViewStats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.eventlink.ViewStats.model.ViewStats;
import ru.eventlink.dto.ViewStatsDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ViewStatsMapper {
    List<ViewStatsDto> listViewStatsToListViewStatsDto(List<ViewStats> viewStats);
}
