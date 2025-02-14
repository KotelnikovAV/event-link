package ru.eventlink.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubCommentDto {
    @NotNull
    Long authorId;
    @NotNull
    Long targetUserId;
    @NotBlank
    String text;
    @NotNull
    LocalDateTime creationDate;
    Set<Long> likesId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SubCommentDto that)) return false;
        return Objects.equals(authorId, that.authorId) &&
                Objects.equals(targetUserId, that.targetUserId) &&
                Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorId, targetUserId, creationDate);
    }
}
