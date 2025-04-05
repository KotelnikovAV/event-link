package ru.eventlink.friends.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.eventlink.friends.model.Friends;
import ru.eventlink.friends.model.FriendsPK;

public interface FriendsRepository extends JpaRepository<Friends, FriendsPK> {

}
