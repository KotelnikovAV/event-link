package ru.eventlink.friends.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.eventlink.friends.model.Friends;
import ru.eventlink.friends.model.FriendsPK;

public interface FriendsRepository extends JpaRepository<Friends, FriendsPK> {
    @Query("SELECT fr " +
            "FROM Friends as fr " +
            "JOIN FETCH fr.user1 " +
            "JOIN FETCH fr.user2 " +
            "WHERE (fr.user1.id = :senderId OR fr.user2.id = :senderId) AND fr.confirmed = false AND fr.initiatorId != :senderId " +
            "ORDER BY fr.requestDate DESC ")
    Page<Friends> findAllFollowersByUserId(Pageable pageable, @Param("senderId") Long senderId);

    @Query("SELECT fr " +
            "FROM Friends as fr " +
            "JOIN FETCH fr.user1 " +
            "JOIN FETCH fr.user2 " +
            "WHERE (fr.user1.id = :senderId OR fr.user2.id = :senderId) AND fr.confirmed = true " +
            "ORDER BY fr.confirmationDate DESC ")
    Page<Friends> findAllFriendsByUserId(Pageable pageable, @Param("senderId") Long senderId);
}
