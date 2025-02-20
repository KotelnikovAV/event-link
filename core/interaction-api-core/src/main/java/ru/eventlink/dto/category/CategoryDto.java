package ru.eventlink.dto.category;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDto {
    long id;
    String name;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CategoryDto that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
