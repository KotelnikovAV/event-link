package ru.eventlink.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.eventlink.enums.Status;

import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequestDto {
    @NotNull
    Set<Long> requestIds;
    @NotNull
    Status status;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventRequestStatusUpdateRequestDto that)) return false;
        return Objects.equals(requestIds, that.requestIds) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestIds, status);
    }
}
