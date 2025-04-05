package ru.eventlink.friends.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.eventlink.dto.friends.FollowUserDto;
import ru.eventlink.dto.friends.FriendUserDto;
import ru.eventlink.exception.NotFoundException;
import ru.eventlink.exception.RestrictionsViolationException;
import ru.eventlink.friends.model.Friends;
import ru.eventlink.friends.model.FriendsPK;
import ru.eventlink.friends.repository.FriendsRepository;
import ru.eventlink.users.model.User;
import ru.eventlink.users.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final FriendsRepository friendsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addFriend(long senderId, long receiverId) {
        log.info("addFriend: user1Id: {}, user2Id: {}", senderId, receiverId);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User with id = " + senderId + " not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("User with id = " + receiverId + " not found"));

        if (friendsRepository.existsById(createFriendsPK(senderId, receiverId))) {
            throw new RestrictionsViolationException("A friend request has already been sent to this user.");
        }

        Friends friends = new Friends();
        friends.setUser1(sender);
        friends.setUser2(receiver);
        friends.setInitiatorId(senderId);
        friends.setConfirmed(false);
        friendsRepository.save(friends);

        receiver.setCountFollowers(receiver.getCountFollowers() + 1);

        log.info("Request has been successfully submitted");
    }

    @Override
    @Transactional
    public void removeFriend(long senderId, long receiverId) {
        log.info("removeFriend: user1Id: {}, user2Id: {}", senderId, receiverId);

        Friends friends = friendsRepository.findById(createFriendsPK(senderId, receiverId))
                .orElseThrow(() ->
                        new NotFoundException("There is no friendship between users: " + senderId + " and " + receiverId));

        User user1 = friends.getUser1();
        User user2 = friends.getUser2();

        if (friends.getConfirmed()) {
            friends.setConfirmed(false);
            friends.setInitiatorId(receiverId);
            user1.setCountFriends(user1.getCountFriends() - 1);
            user2.setCountFriends(user2.getCountFriends() - 1);

            if (user1.getId().equals(senderId)) {
                user1.setCountFollowers(user1.getCountFollowers() + 1);
            } else if (user2.getId().equals(senderId)) {
                user2.setCountFollowers(user2.getCountFollowers() + 1);
            }
        } else {
            if (user1.getId().equals(friends.getInitiatorId())) {
                user2.setCountFollowers(user2.getCountFollowers() - 1);
            } else if (user2.getId().equals(friends.getInitiatorId())) {
                user1.setCountFollowers(user1.getCountFollowers() - 1);
            }

            friendsRepository.delete(friends);
        }

        log.info("Request has been successfully removed");
    }

    @Override
    public void confirmRequest(long user1Id, long user2Id) {

    }

    @Override
    public List<FollowUserDto> findAllFollowers(long userId, int page, int size) {
        return List.of();
    }

    @Override
    public List<FriendUserDto> findAllFriends(long userId, int page, int size) {
        return List.of();
    }

    @Override
    public List<FollowUserDto> findAllUserFollow(long userId, int page, int size) {
        return List.of();
    }

    private FriendsPK createFriendsPK(long user1Id, long user2Id) {
        if (user1Id < user2Id) {
            return new FriendsPK(user1Id, user2Id);
        } else {
            return new FriendsPK(user2Id, user1Id);
        }
    }
}
