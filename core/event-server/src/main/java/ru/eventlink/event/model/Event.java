package ru.eventlink.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.proxy.HibernateProxy;
import ru.eventlink.category.model.Category;
import ru.eventlink.enums.State;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Column
    String annotation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @NotNull
    @Column
    LocalDateTime createdOn;

    @NotBlank
    @Column
    String description;

    @NotNull
    @Column
    LocalDateTime eventDate;

    @NotNull
    @Column
    Long initiatorId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    Location location;

    @NotNull
    @Column
    Boolean paid;

    @NotNull
    @Column
    Long participantLimit;

    @NotNull
    @Column
    LocalDateTime publishedOn;

    @NotNull
    @Column
    Boolean requestModeration;

    @NotNull
    @Column
    @Enumerated(value = EnumType.STRING)
    State state;

    @NotBlank
    @Column
    String title;

    @Column(name = "confirmed_requests")
    Long confirmedRequests;

    @NotNull
    @Column
    Long likes;

    @Transient
    Double rating;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Event event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "rating = " + rating + ", " +
                "likes = " + likes + ", " +
                "confirmedRequests = " + confirmedRequests + ", " +
                "title = " + title + ", " +
                "state = " + state + ", " +
                "requestModeration = " + requestModeration + ", " +
                "publishedOn = " + publishedOn + ", " +
                "participantLimit = " + participantLimit + ", " +
                "paid = " + paid + ", " +
                "location = " + location + ", " +
                "initiatorId = " + initiatorId + ", " +
                "eventDate = " + eventDate + ", " +
                "description = " + description + ", " +
                "createdOn = " + createdOn + ", " +
                "annotation = " + annotation + ")";
    }
}
