package ru.eventlink.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    List<Long> events;
    Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50)
    String title;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NewCompilationDto that)) return false;
        return Objects.equals(events, that.events) && Objects.equals(pinned, that.pinned) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(events, pinned, title);
    }
}
