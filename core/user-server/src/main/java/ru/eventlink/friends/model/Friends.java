package ru.eventlink.friends.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import ru.eventlink.exception.RestrictionsViolationException;
import ru.eventlink.users.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "friends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Friends {
    @EmbeddedId
    private FriendsPK id;

    @ManyToOne
    @MapsId("user1Id")
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne
    @MapsId("user2Id")
    @JoinColumn(name = "user2_id")
    private User user2;

    @Column
    private Long initiatorId;

    @Column
    private Boolean confirmed;

    @Column
    private LocalDateTime requestDate;

    @Column
    private LocalDateTime confirmationDate;

    @PrePersist
    public void onCreate() {
        if (!(initiatorId.equals(user1.getId()) || initiatorId.equals(user2.getId()))) {
            throw new RestrictionsViolationException("The initiator's id must be equal to user1 or user1");
        }

        if (user1.getId() > user2.getId()) {
            User temp = user1;
            user1 = user2;
            user2 = temp;

            this.id.setUser1Id(user1.getId());
            this.id.setUser2Id(user2.getId());
        }

        requestDate = LocalDateTime.now();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Friends friends = (Friends) o;
        return getId() != null && Objects.equals(getId(), friends.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "EmbeddedId = " + id + ")";
    }
}
