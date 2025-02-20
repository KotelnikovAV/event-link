package ru.eventlink.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.eventlink.utility.Constants;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    @Positive
    Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    LocalDateTime eventDate;
    @NotNull
    LocationDto location;
    Boolean paid;
    @PositiveOrZero
    Long participantLimit;
    Boolean requestModeration;
    @NotBlank
    @Size(min = 3, max = 120)
    String title;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NewEventDto that)) return false;
        return Objects.equals(annotation, that.annotation) && Objects.equals(eventDate, that.eventDate) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, eventDate, location);
    }
}
