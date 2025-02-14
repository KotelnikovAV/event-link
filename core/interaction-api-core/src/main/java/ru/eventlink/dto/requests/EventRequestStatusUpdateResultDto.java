package ru.eventlink.dto.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResultDto {
    List<ParticipationRequestDto> confirmedRequests;
    List<ParticipationRequestDto> rejectedRequests;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventRequestStatusUpdateResultDto resultDto)) return false;
        return Objects.equals(confirmedRequests, resultDto.confirmedRequests) && Objects.equals(rejectedRequests, resultDto.rejectedRequests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(confirmedRequests, rejectedRequests);
    }
}
