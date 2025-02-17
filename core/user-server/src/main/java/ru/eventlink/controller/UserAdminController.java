package ru.eventlink.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.client.user.UserClient;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.dto.user.UserRequestDto;
import ru.eventlink.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserAdminController implements UserClient {
    private final UserService userService;

    @GetMapping
    @Validated
    @Override
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get all users");
        return userService.getAllUsers(ids, page, size);
    }

    @GetMapping("/rating")
    @Override
    public List<UserDto> getAllUsersBySortRating(@RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get all users by sort rating");
        return userService.getAllUsersBySortRating(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public UserDto createUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        log.info("Create user: {}", userRequestDto);
        return userService.createUser(userRequestDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void deleteUser(@PathVariable long userId) {
        log.info("Delete user: {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}/existence")
    @Override
    public boolean getUserExists(@PathVariable long userId) {
        log.info("Get user exists: {}", userId);
        return userService.getUserExists(userId);
    }

    @PutMapping("/{userId}")
    @Override
    public void updateRatingUser(@PathVariable long userId,
                                 @RequestParam int rating) {
        log.info("Update user: {}", userId);
        userService.updateRatingUser(userId, rating);
    }
}
