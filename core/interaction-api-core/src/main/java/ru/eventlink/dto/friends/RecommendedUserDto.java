package ru.eventlink.dto.friends;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecommendedUserDto {
    @NotNull
    private Long id;
    @NotEmpty
    private String email;
    @NotEmpty
    private String name;
    @NotNull
    private Integer rating;
    @NotNull
    private Integer countFollowers;
    @NotNull
    private Integer countFriends;
    @NotNull
    private Long commonFriendsCount;
}
