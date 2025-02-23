package ru.eventlink.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestCommentDto {
    @NotNull
    Long eventId;
    @NotNull
    Long authorId;
    @NotBlank
    @Size(max = 1500)
    String text;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RequestCommentDto that)) return false;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(authorId, that.authorId) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, authorId, text);
    }
}
