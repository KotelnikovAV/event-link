package ru.eventlink.friends.service;

import ru.eventlink.dto.friends.FollowUserDto;
import ru.eventlink.dto.friends.FriendUserDto;
import ru.eventlink.dto.friends.RecommendedUserDto;

import java.util.List;

public interface FriendsService {
    void addFriend(long senderId, long receiverId);

    void removeFriend(long senderId, long receiverId);

    void confirmRequest(long senderId, long receiverId);

    List<FollowUserDto> findAllFollowers(long senderId, int page, int size);

    List<FriendUserDto> findAllFriends(long senderId, int page, int size);

    List<RecommendedUserDto> findRecommendationFriends(long senderId, int page, int size);
}
