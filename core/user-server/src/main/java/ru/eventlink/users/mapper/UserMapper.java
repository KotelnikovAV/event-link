package ru.eventlink.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.dto.user.UserRequestDto;
import ru.eventlink.users.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDto userToUserDto(User user);

    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "countFollowers", ignore = true)
    @Mapping(target = "countFriends", ignore = true)
    User userRequestDtoToUser(UserRequestDto userRequestDto);

    List<UserDto> listUserToListUserDto(List<User> users);
}
