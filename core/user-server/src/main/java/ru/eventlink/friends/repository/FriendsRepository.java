package ru.eventlink.friends.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.eventlink.dto.friends.RecommendedUserDto;
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

    @Query(value = """
            WITH user_friends AS (
    SELECT
        CASE
            WHEN user1_id = :userId THEN user2_id
            ELSE user1_id
        END AS friend_id
    FROM friends
    WHERE (user1_id = :userId OR user2_id = :userId)
      AND confirmed = true
),
potential_friends AS (
    SELECT
        CASE
            WHEN f.user1_id = uf.friend_id THEN f.user2_id
            ELSE f.user1_id
        END AS recommended_id,
        COUNT(*) AS common_friends_count
    FROM friends f
    JOIN user_friends uf
      ON f.user1_id = uf.friend_id OR f.user2_id = uf.friend_id
    WHERE
        (f.user1_id != :userId AND f.user2_id != :userId)
        AND confirmed = true
    GROUP BY recommended_id
)
SELECT
    u.id                  AS id,
    u.email               AS email,
    u.name                AS name,
    u.rating              AS rating,
    u.count_followers     AS countFollowers,
    u.count_friends       AS countFriends,
    p.common_friends_count AS commonFriendsCount
FROM potential_friends p
JOIN users u ON p.recommended_id = u.id
WHERE
    p.recommended_id NOT IN (SELECT friend_id FROM user_friends)
    AND p.recommended_id != :userId
ORDER BY p.common_friends_count DESC;
""", nativeQuery = true)
    Page<RecommendedUserDto> findFriendRecommendations(Pageable pageable, @Param("userId") Long userId);
}
