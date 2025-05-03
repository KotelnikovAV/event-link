package ru.eventlink.client;

import ru.eventlink.dto.user.UserDto;

import java.util.List;

public interface RestClient {
    void checkUserAndEventExists(Long userId, Long eventId);

    List<UserDto> getAllUsers(List<Long> usersId, int page, int pageSize);

    boolean getUserExists(long userId);
}
