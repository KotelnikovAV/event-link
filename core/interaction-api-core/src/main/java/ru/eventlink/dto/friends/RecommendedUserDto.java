package ru.eventlink.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecommendedUserDto {
    private Long id;
    private String email;
    private String name;
    private Integer rating;
    private Integer countFollowers;
    private Integer countFriends;
    private Long commonFriendsCount;
}
