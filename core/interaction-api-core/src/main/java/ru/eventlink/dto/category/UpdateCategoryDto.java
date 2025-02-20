package ru.eventlink.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCategoryDto {
    @NotBlank
    @Length(min = 1, max = 50)
    String name;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UpdateCategoryDto that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
