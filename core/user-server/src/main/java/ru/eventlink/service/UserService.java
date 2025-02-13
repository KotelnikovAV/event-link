package ru.eventlink.service;

import ru.eventlink.dto.user.UserDto;
import ru.eventlink.dto.user.UserRequestDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(List<Long> ids, int from, int size);

    List<UserDto> getAllUsersBySortRating(int from, int size);

    UserDto createUser(UserRequestDto requestDto);

    void deleteUser(long userId);

    boolean getUserExists(long userId);

    void updateRatingUser(long userId, int rating);
}
