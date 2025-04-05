package ru.eventlink.dto.friends;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendUserDto {
    private Long userId;
    private LocalDateTime requestDate;
    private LocalDateTime confirmationDate;
}
