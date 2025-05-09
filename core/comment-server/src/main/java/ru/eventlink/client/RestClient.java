package ru.eventlink.client;

import ru.eventlink.dto.user.UserDto;

import java.util.List;

public interface RestClient {
    boolean getEventExists(Long userId);

    List<UserDto> getAllUsers(List<Long> usersId, int page, int pageSize);

    boolean getUserExists(long userId);
}
