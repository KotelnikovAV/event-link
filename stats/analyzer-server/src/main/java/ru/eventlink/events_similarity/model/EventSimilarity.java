package ru.eventlink.events_similarity.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Entity
@Table(name = "event_similarity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarity {
    @EmbeddedId
    EventSimilarityId eventSimilarityId;

    @NotNull
    @Column
    Double score;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventSimilarity that)) return false;
        return Objects.equals(eventSimilarityId, that.eventSimilarityId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(eventSimilarityId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "EmbeddedId = " + eventSimilarityId + ", " +
                "score = " + score + ")";
    }
}
