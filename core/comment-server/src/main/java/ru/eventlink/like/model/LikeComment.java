package ru.eventlink.like.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.ProxyUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(value = "likes")
@CompoundIndexes({
        @CompoundIndex(name = "commentId_authorId_idx", def = "{'commentId': 1, 'authorId': 1}")
})
public class LikeComment {
    @Id
    ObjectId id;
    @NotNull(message = "commentId must not be null")
    ObjectId commentId;
    @NotNull(message = "authorId must not be null")
    Long authorId;
    @NotNull
    @CreatedDate
    LocalDateTime creationDate;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ProxyUtils.getUserClass(this) != ProxyUtils.getUserClass(o))
            return false;
        LikeComment that = (LikeComment) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return ProxyUtils.getUserClass(this).hashCode();
    }
}
