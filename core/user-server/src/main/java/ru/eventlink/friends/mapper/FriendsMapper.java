package ru.eventlink.friends.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.eventlink.dto.friends.FollowUserDto;
import ru.eventlink.dto.friends.FriendUserDto;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.friends.model.Friends;
import ru.eventlink.users.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FriendsMapper {

    @Mapping(target = "user", expression = "java(getUserDto(friends))")
    FollowUserDto friendsToFollowUserDto(Friends friends);

    List<FollowUserDto> listFriendsToListFollowUserDto(List<Friends> friends);

    @Mapping(target = "user", expression = "java(getUserDto(friends))")
    FriendUserDto friendsToFriendUserDto(Friends friends);

    List<FriendUserDto> listFriendsToListFriendUserDto(List<Friends> friends);

    default UserDto getUserDto(Friends friends) {
        UserDto userDto = new UserDto();
        User user;

        if (friends.getUser1() == null) {
            user = friends.getUser2();
        } else {
            user = friends.getUser1();
        }

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRating(user.getRating());
        userDto.setCountFriends(user.getCountFriends());
        userDto.setCountFollowers(user.getCountFollowers());
        return userDto;
    }
}
