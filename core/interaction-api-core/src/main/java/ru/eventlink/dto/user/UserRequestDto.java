package ru.eventlink.dto.user;

import jakarta.validation.constraints.Email;
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
public class UserRequestDto {
    @NotBlank
    @Length(min = 6, max = 254)
    @Email
    String email;
    @NotBlank
    @Length(min = 2, max = 250)
    String name;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserRequestDto that)) return false;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}
