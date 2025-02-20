package ru.eventlink.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendationsDto {
    @NotNull
    Long eventId;
    @NotNull
    Double score;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RecommendationsDto that)) return false;
        return Objects.equals(eventId, that.eventId) && Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, score);
    }
}
