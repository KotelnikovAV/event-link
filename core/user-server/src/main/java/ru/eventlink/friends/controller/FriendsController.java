package ru.eventlink.friends.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.eventlink.dto.friends.FollowUserDto;
import ru.eventlink.dto.friends.FriendUserDto;
import ru.eventlink.dto.friends.RecommendedUserDto;
import ru.eventlink.friends.service.FriendsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{senderId}")
@RequiredArgsConstructor
@Slf4j
public class FriendsController {
    private final FriendsService friendsService;

    @PostMapping("/friends/{receiverId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addFriend(@PathVariable @Positive long senderId, @PathVariable @Positive long receiverId) {
        log.info("Adding friend with id = {}", receiverId);
        friendsService.addFriend(senderId, receiverId);
    }

    @DeleteMapping("/friends/{receiverId}")
    public void removeFriend(@PathVariable @Positive long senderId, @PathVariable @Positive long receiverId) {
        log.info("Removing friend with id = {}", receiverId);
        friendsService.removeFriend(senderId, receiverId);
    }

    @PutMapping("/friends/{receiverId}")
    public void confirmRequest(@PathVariable @Positive long senderId, @PathVariable @Positive long receiverId) {
        log.info("Confirming request from user with id = {}", senderId);
        friendsService.confirmRequest(senderId, receiverId);
    }

    @GetMapping("/followers")
    public List<FollowUserDto> findAllFollowers(@PathVariable @Positive long senderId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Search for all followers of a user with id = {}", senderId);
        return friendsService.findAllFollowers(senderId, page, size);
    }

    @GetMapping("/friends")
    public List<FriendUserDto> findAllFriends(@PathVariable @Positive long senderId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Search for all friends of a user with id = {}", senderId);
        return friendsService.findAllFriends(senderId, page, size);
    }

    @GetMapping("/friends/recommendations")
    public List<RecommendedUserDto> findRecommendationFriends(@PathVariable @Positive long senderId,
                                                              @RequestParam(defaultValue = "0") @PositiveOrZero int page,
                                                              @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Search recommendations friends for user with id = {}", senderId);
        return friendsService.findRecommendationFriends(senderId, page, size);
    }
}
