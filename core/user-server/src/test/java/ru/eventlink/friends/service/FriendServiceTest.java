package ru.eventlink.friends.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class FriendServiceTest {

    @Autowired
    private FriendsService friendsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    private final List<User> users = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        users.clear();

        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setEmail("email = " + i);
            user.setName("name = " + i);
            user.setRating(0);
            user.setCountFriends(0);
            user.setCountFollowers(0);

            users.add(user);
        }
    }

    @Test
    void testAddFriendWithValidUsersWhenSenderIdMoreReceiverId() {
        long[] usersId = getUsersId(2);

        friendsService.addFriend(usersId[0], usersId[1]);
        User receiver = userRepository.findById(usersId[1])
                .orElseThrow(() -> new NotFoundException("User with id = " + usersId[1] + " not found"));
        User sender = userRepository.findById(usersId[0])
                .orElseThrow(() -> new NotFoundException("User with id = " + usersId[0] + " not found"));
        Friends friends = friendsRepository.findAll().getFirst();

        assertThat(receiver.getCountFollowers(), equalTo(1));
        assertThat(receiver.getCountFriends(), equalTo(0));
        assertThat(sender.getCountFollowers(), equalTo(0));
        assertThat(sender.getCountFriends(), equalTo(0));
        assertThat(friends.getConfirmed(), equalTo(false));
        assertThat(friends.getInitiatorId(), equalTo(usersId[0]));
        assertThat(friends.getRequestDate(), notNullValue());
        assertThat(friends.getUser1().getId(), lessThan(friends.getUser2().getId()));
    }

    @Test
    void testAddFriendWithValidUsersWhenSenderIdLessReceiverId() {
        long[] usersId = getUsersId(2);
        long senderId = usersId[0];
        long receiverId = usersId[1];

        friendsService.addFriend(receiverId, senderId);
        User receiver = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User with id = " + senderId + " not found"));
        User sender = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("User with id = " + receiverId + " not found"));
        Friends friends = friendsRepository.findAll().getFirst();

        assertThat(receiver.getCountFollowers(), equalTo(1));
        assertThat(receiver.getCountFriends(), equalTo(0));
        assertThat(sender.getCountFollowers(), equalTo(0));
        assertThat(sender.getCountFriends(), equalTo(0));
        assertThat(friends.getConfirmed(), equalTo(false));
        assertThat(friends.getInitiatorId(), equalTo(usersId[1]));
        assertThat(friends.getRequestDate(), notNullValue());
        assertThat(friends.getUser1().getId(), lessThan(friends.getUser2().getId()));
    }

    @Test
    void testAddFriendWithNotValidUsers() {
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> friendsService.addFriend(Long.MAX_VALUE, Long.MAX_VALUE - 1L))
                .withMessage("User with id = " + Long.MAX_VALUE + " not found");
    }

    @Test
    void testAddFriendWithEqualUsersId() {
        assertThatExceptionOfType(RestrictionsViolationException.class)
                .isThrownBy(() -> friendsService.addFriend(Long.MAX_VALUE, Long.MAX_VALUE))
                .withMessage("Friends can't be the same as the sender");
    }

    @Test
    void testAddFriendWithNotValidFriendsPK() {
        long[] usersId = getUsersId(2);
        long senderId = usersId[0];
        long receiverId = usersId[1];

        friendsService.addFriend(senderId, receiverId);

        assertThatExceptionOfType(RestrictionsViolationException.class)
                .isThrownBy(() -> friendsService.addFriend(senderId, receiverId))
                .withMessage("A friend request has already been sent to this user.");
    }

    @Test
    void testValidConfirmRequest() throws InterruptedException {
        long[] usersId = getUsersId(2);
        long senderId = usersId[0];
        long receiverId = usersId[1];

        friendsService.addFriend(senderId, receiverId);
        Thread.sleep(50L);
        friendsService.confirmRequest(receiverId, senderId);
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("User with id = " + receiverId + " not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User with id = " + senderId + " not found"));
        Friends friends = friendsRepository.findAll().getFirst();

        assertThat(receiver.getCountFriends(), equalTo(1));
        assertThat(receiver.getCountFollowers(), equalTo(0));
        assertThat(sender.getCountFriends(), equalTo(1));
        assertThat(sender.getCountFollowers(), equalTo(0));
        assertThat(friends.getConfirmed(), equalTo(true));
        assertThat(friends.getRequestDate().isBefore(friends.getConfirmationDate()), equalTo(true));
    }

    @Test
    void testConfirmRequestWithNotValidInitiator() {
        long[] usersId = getUsersId(2);
        long senderId = usersId[0];
        long receiverId = usersId[1];

        friendsService.addFriend(senderId, receiverId);

        assertThatExceptionOfType(RestrictionsViolationException.class)
                .isThrownBy(() -> friendsService.confirmRequest(senderId, receiverId))
                .withMessage("The user cannot confirm the request by himself");
    }

    @Test
    void testConfirmRequestWithNotValidConfirmed() {
        long[] usersId = getUsersId(2);
        long senderId = usersId[0];
        long receiverId = usersId[1];

        friendsService.addFriend(senderId, receiverId);
        friendsService.confirmRequest(receiverId, senderId);

        assertThatExceptionOfType(RestrictionsViolationException.class)
                .isThrownBy(() -> friendsService.confirmRequest(receiverId, senderId))
                .withMessage("This user is already in the friends list");
    }

    @Test
    void testRemoveFriendWithoutConfirmed() {
        long[] usersId = getUsersId(2);
        long senderId = usersId[0];
        long receiverId = usersId[1];

        friendsService.addFriend(senderId, receiverId);
        friendsService.removeFriend(senderId, receiverId);
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("User with id = " + receiverId + " not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User with id = " + senderId + " not found"));
        long count = friendsRepository.count();

        assertThat(receiver.getCountFollowers(), equalTo(0));
        assertThat(receiver.getCountFriends(), equalTo(0));
        assertThat(sender.getCountFollowers(), equalTo(0));
        assertThat(sender.getCountFriends(), equalTo(0));
        assertThat(count, equalTo(0L));
    }

    @Test
    void testRemoveFriendWithConfirmed() {
        long[] usersId = getUsersId(2);
        long senderId = usersId[0];
        long receiverId = usersId[1];

        friendsService.addFriend(senderId, receiverId);
        friendsService.confirmRequest(receiverId, senderId);
        friendsService.removeFriend(senderId, receiverId);
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("User with id = " + receiverId + " not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User with id = " + senderId + " not found"));
        Friends friends = friendsRepository.findAll().getFirst();

        assertThat(receiver.getCountFollowers(), equalTo(0));
        assertThat(receiver.getCountFriends(), equalTo(0));
        assertThat(sender.getCountFollowers(), equalTo(1));
        assertThat(sender.getCountFriends(), equalTo(0));
        assertThat(friends.getConfirmed(), equalTo(false));
        assertThat(friends.getRequestDate(), nullValue());
    }

    @Test
    void testFindAllFollowers() {
        long[] usersId = getUsersId(users.size());
        long senderId = usersId[0];
        prepareTestDataForFindFriends(usersId, false);

        List<FollowUserDto> followsUser = friendsService.findAllFollowers(senderId, 0, users.size());
        User receiver = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User with id = " + senderId + " not found"));
        long[] usersIdFromFollowsUser = followsUser.stream()
                .mapToLong(follower -> follower.getUser().getId())
                .sorted()
                .toArray();

        assertThat(followsUser.size(), equalTo(users.size() - 1));
        assertThat(receiver.getCountFollowers(), equalTo(users.size() - 1));
        assertThat(receiver.getCountFriends(), equalTo(0));

        for (int i = 1; i < usersId.length; i++) {
            assertThat(usersIdFromFollowsUser[i - 1], equalTo(usersId[i]));
        }
    }

    @Test
    void testFindAllFriends() {
        long[] usersId = getUsersId(users.size());
        long senderId = usersId[0];
        prepareTestDataForFindFriends(usersId, true);

        List<FriendUserDto> friendsUser = friendsService.findAllFriends(senderId, 0, users.size());
        User receiver = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("User with id = " + senderId + " not found"));
        long[] usersIdFromFriendsUser = friendsUser.stream()
                .mapToLong(follower -> follower.getUser().getId())
                .sorted()
                .toArray();

        assertThat(friendsUser.size(), equalTo(users.size() - 1));
        assertThat(receiver.getCountFriends(), equalTo(users.size() - 1));
        assertThat(receiver.getCountFollowers(), equalTo(0));

        for (int i = 1; i < usersId.length; i++) {
            assertThat(usersIdFromFriendsUser[i - 1], equalTo(usersId[i]));
        }
    }

//    @Test
//    void testFindRecommendationFriends() {
//        long[] usersId = getUsersId(users.size());
//        prepareTestDataForRecommendations();
//
//        List<RecommendedUserDto> recommendations = friendsService.findRecommendationFriends(1L, 0, users.size());
//    }

    private long[] getUsersId(int count) {
        if (count > users.size()) {
            throw new RuntimeException("Count is greater than users count");
        }

        List<User> usersForSave = users.stream()
                .limit(count)
                .toList();

        return userRepository.saveAll(usersForSave).stream()
                .mapToLong(User::getId)
                .sorted()
                .toArray();
    }

    private void prepareTestDataForFindFriends(long[] usersId, boolean confirmed) {
        for (int i = 1; i < usersId.length; i++) {
            friendsService.addFriend(usersId[i], usersId[0]);

            if (confirmed) {
                friendsService.confirmRequest(usersId[0], usersId[i]);
            }
        }
    }

    private void prepareTestDataForRecommendations() {
        Map<Long, User> usersWithId = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        Friends friends1 = createFriends(usersWithId.get(1L), usersWithId.get(2L));
        Friends friends2 = createFriends(usersWithId.get(2L), usersWithId.get(3L));
        Friends friends3 = createFriends(usersWithId.get(2L), usersWithId.get(4L));
        Friends friends4 = createFriends(usersWithId.get(3L), usersWithId.get(5L));
        friendsRepository.saveAll(List.of(friends1, friends2, friends3, friends4));
    }

    private Friends createFriends(User user1, User user2) {
        Friends friends = new Friends();
        FriendsPK friendsPK = new FriendsPK(user1.getId(), user2.getId());
        friends.setId(friendsPK);
        friends.setUser1(user1);
        friends.setUser2(user2);
        friends.setInitiatorId(user1.getId());
        friends.setConfirmed(true);
        friends.setRequestDate(LocalDateTime.now().minusDays(1));
        friends.setConfirmationDate(LocalDateTime.now());
        return friends;
    }


}
