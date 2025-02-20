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
public class LocationDto {
    Long id;
    @NotNull
    Float lat;
    @NotNull
    Float lon;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LocationDto that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
