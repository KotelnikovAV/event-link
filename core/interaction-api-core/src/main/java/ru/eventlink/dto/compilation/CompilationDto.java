package ru.eventlink.dto.compilation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.eventlink.dto.event.EventShortDto;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    List<EventShortDto> events;
    Long id;
    Boolean pinned;
    String title;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CompilationDto that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
