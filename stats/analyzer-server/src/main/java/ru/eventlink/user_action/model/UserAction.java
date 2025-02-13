package ru.eventlink.user_action.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_action")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAction {
    @EmbeddedId
    UserActionId userActionId;

    @NotNull
    @Column
    @Enumerated(value = EnumType.STRING)
    ActionType actionType;

    @NotNull
    @Column
    LocalDateTime actionDate;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserAction that)) return false;
        return Objects.equals(userActionId, that.userActionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userActionId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "EmbeddedId = " + userActionId + ", " +
                "actionType = " + actionType + ", " +
                "actionDate = " + actionDate + ")";
    }
}
