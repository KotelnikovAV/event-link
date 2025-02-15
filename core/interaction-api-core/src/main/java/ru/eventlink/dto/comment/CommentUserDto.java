package ru.eventlink.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentUserDto {
    @NotNull
    Long authorId;
    @NotBlank
    String text;
    @NotNull
    LocalDateTime creationDate;
    @NotNull
    LocalDateTime updateDate;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CommentUserDto that)) return false;
        return Objects.equals(authorId, that.authorId) && Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorId, creationDate);
    }
}
