package ru.eventlink.friends.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FriendsPK implements Serializable {
    @Column(name = "user1_id")
    private Long user1Id;

    @Column(name = "user2_id")
    private Long user2Id;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FriendsPK friendsPK)) return false;
        return Objects.equals(user1Id, friendsPK.user1Id) && Objects.equals(user2Id, friendsPK.user2Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1Id, user2Id);
    }
}
