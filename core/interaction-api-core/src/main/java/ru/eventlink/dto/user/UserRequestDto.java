package ru.eventlink.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
public class UserRequestDto {
    @NotBlank
    @Length(min = 6, max = 254)
    @Email
    String email;
    @NotBlank
    @Length(min = 2, max = 250)
    String name;
}
