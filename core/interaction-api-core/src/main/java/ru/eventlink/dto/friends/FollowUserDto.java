package ru.eventlink.dto.friends;

import lombok.*;
import ru.eventlink.dto.user.UserDto;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FollowUserDto {
    private UserDto user;
    private LocalDateTime requestDate;
}
