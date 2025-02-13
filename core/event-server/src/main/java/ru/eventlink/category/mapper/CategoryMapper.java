package ru.eventlink.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.eventlink.category.model.Category;
import ru.eventlink.dto.category.CategoryDto;
import ru.eventlink.dto.category.NewCategoryDto;
import ru.eventlink.dto.category.UpdateCategoryDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

    CategoryDto categoryToCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category updateCategoryDtoToCategory(UpdateCategoryDto updateCategoryDto);

    List<CategoryDto> listCategoryToListCategoryDto(List<Category> categoryList);
}
