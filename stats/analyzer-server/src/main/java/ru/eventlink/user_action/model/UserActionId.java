package ru.eventlink.user_action.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class UserActionId implements Serializable {
    @NotNull
    @JoinColumn(name = "user_id")
    Long userId;

    @NotNull
    @JoinColumn(name = "event_id")
    Long eventId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserActionId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "userId = " + userId + ", " +
                "eventId = " + eventId + ")";
    }
}