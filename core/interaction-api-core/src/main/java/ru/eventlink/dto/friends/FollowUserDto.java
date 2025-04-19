package ru.eventlink.dto.friends;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.utility.Constants;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FollowUserDto {
    private UserDto user;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime requestDate;
}
