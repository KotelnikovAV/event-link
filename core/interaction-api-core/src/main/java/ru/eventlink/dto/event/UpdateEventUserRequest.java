package ru.eventlink.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.eventlink.enums.StateActionUser;
import ru.eventlink.utility.Constants;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    String annotation;
    @Positive
    Long category;
    @Size(min = 20, max = 7000)
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    LocalDateTime eventDate;
    LocationDto location;
    Boolean paid;
    @PositiveOrZero
    Long participantLimit;
    Boolean requestModeration;
    @Size(min = 3, max = 120)
    String title;
    StateActionUser stateAction;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UpdateEventUserRequest that)) return false;
        return Objects.equals(annotation, that.annotation) && Objects.equals(eventDate, that.eventDate) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, eventDate, location);
    }
}
