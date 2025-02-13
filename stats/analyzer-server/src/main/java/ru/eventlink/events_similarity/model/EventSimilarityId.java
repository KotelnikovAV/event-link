package ru.eventlink.events_similarity.model;

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
public class EventSimilarityId implements Serializable {
    @NotNull
    @JoinColumn(name = "event_A")
    Long eventA;

    @NotNull
    @JoinColumn(name = "event_B")
    Long eventB;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventSimilarityId that)) return false;
        return Objects.equals(eventA, that.eventA) && Objects.equals(eventB, that.eventB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventA, eventB);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "eventA = " + eventA + ", " +
                "eventB = " + eventB + ")";
    }
}