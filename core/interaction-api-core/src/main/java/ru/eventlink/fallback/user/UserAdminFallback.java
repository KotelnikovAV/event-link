package ru.eventlink.fallback.user;

import org.springframework.stereotype.Component;
import ru.eventlink.client.user.UserAdminClient;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.dto.user.UserRequestDto;
import ru.eventlink.exception.ServerUnavailableException;

import java.util.List;

@Component
public class UserAdminFallback implements UserAdminClient {
    @Override
    public List<UserDto> getAllUsers(List<Long> ids, Integer page, Integer size) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/users method GET is unavailable");
    }

    @Override
    public List<UserDto> getAllUsersBySortRating(Integer page, Integer size) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/users/rating method GET is unavailable");
    }

    @Override
    public UserDto createUser(UserRequestDto userRequestDto) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/users method POST is unavailable");
    }

    @Override
    public void deleteUser(long userId) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/users/{userId} method DELETE is unavailable");
    }

    @Override
    public boolean getUserExists(long userId) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/users/{userId}/existence method GET is unavailable");
    }

    @Override
    public void updateRatingUser(long userId, int rating) {
        throw new ServerUnavailableException("Endpoint /api/v1/admin/users/{userId} method PATCH is unavailable");
    }
}
