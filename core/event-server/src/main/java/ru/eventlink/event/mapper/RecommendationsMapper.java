package ru.eventlink.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.eventlink.dto.event.RecommendationsDto;
import ru.eventlink.stats.proto.RecommendedEventProto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecommendationsMapper {
    RecommendationsDto recommendedEventProtoToRecommendationsDto(RecommendedEventProto recommendedEventProto);

    List<RecommendationsDto> listRecommendedEventProtoToListRecommendationsDto(List<RecommendedEventProto> recommendedEventProto);
}
