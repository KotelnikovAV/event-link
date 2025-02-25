package ru.eventlink.comment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.ProxyUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(value = "comments")
public class Comment {
    @Id
    ObjectId id;
    @NotNull(message = "eventId must not be null")
    @Indexed
    Long eventId;
    @NotNull(message = "authorId must not be null")
    Long authorId;
    @NotBlank(message = "text must not be blank")
    String text;
    @NotNull
    Integer countResponse;
    ObjectId parentCommentId;
    @NotNull
    @CreatedDate
    LocalDateTime creationDate;
    @NotNull
    @CreatedDate
    LocalDateTime updateDate;
    Integer likes;
    List<Long> likedUsersId = new ArrayList<>();
    @NotNull
    Boolean deleted;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ProxyUtils.getUserClass(this) != ProxyUtils.getUserClass(o))
            return false;
        Comment comment = (Comment) o;
        return getId() != null && Objects.equals(getId(), comment.getId());
    }

    @Override
    public final int hashCode() {
        return ProxyUtils.getUserClass(this).hashCode();
    }
}
