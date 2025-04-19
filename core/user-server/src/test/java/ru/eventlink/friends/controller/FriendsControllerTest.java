package ru.eventlink.friends.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.eventlink.dto.friends.FollowUserDto;
import ru.eventlink.dto.friends.FriendUserDto;
import ru.eventlink.dto.friends.RecommendedUserDto;
import ru.eventlink.dto.user.UserDto;
import ru.eventlink.friends.service.FriendsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FriendsController.class)
public class FriendsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FriendsService friendsService;

    private static final LocalDateTime requestDate = LocalDateTime.now().minusDays(1L);

    private static final LocalDateTime confirmationDate = LocalDateTime.now();

    private static final List<FollowUserDto> followsUserDto = new ArrayList<>();

    private static final List<FriendUserDto> friendsUserDto = new ArrayList<>();

    private static final List<RecommendedUserDto> recommendationsUserDto = new ArrayList<>();

    private final Long senderId = 1L;

    private final Long receiverId = 2L;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @BeforeAll
    public static void setUp() {
        for (int i = 0; i < 10; i++) {
            UserDto userDto = UserDto.builder()
                    .id((long) i)
                    .email("email@email.com")
                    .name("name")
                    .rating(0)
                    .countFollowers(0)
                    .countFriends(0)
                    .build();

            FollowUserDto followUserDto = FollowUserDto.builder()
                    .user(userDto)
                    .requestDate(requestDate)
                    .build();

            FriendUserDto friendUserDto = FriendUserDto.builder()
                    .user(userDto)
                    .requestDate(requestDate)
                    .confirmationDate(confirmationDate)
                    .build();

            RecommendedUserDto recommendedUserDto = new RecommendedUserDto(
                    (long) i,
                    "email@email.com",
                    "name",
                    0,
                    0,
                    0,
                    (long) (10 - i)
            );

            friendsUserDto.add(friendUserDto);
            followsUserDto.add(followUserDto);
            recommendationsUserDto.add(recommendedUserDto);
        }

    }

    @Test
    void testAddFriend() throws Exception {
        doNothing().when(friendsService).addFriend(anyLong(), anyLong());

        mvc.perform(post("/api/v1/users/{senderId}/friends/{receiverId}", senderId, receiverId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(friendsService).addFriend(senderId, receiverId);
    }

    @Test
    void testDeleteFriend() throws Exception {
        doNothing().when(friendsService).removeFriend(anyLong(), anyLong());

        mvc.perform(delete("/api/v1/users/{senderId}/friends/{receiverId}", senderId, receiverId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendsService).removeFriend(senderId, receiverId);
    }

    @Test
    void testConfirmRequest() throws Exception {
        doNothing().when(friendsService).confirmRequest(anyLong(), anyLong());

        mvc.perform(put("/api/v1/users/{senderId}/friends/{receiverId}", senderId, receiverId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendsService).confirmRequest(senderId, receiverId);
    }

    @Test
    void testFindAllFollowers() throws Exception {
        when(friendsService.findAllFollowers(anyLong(), anyInt(), anyInt()))
                .thenReturn(followsUserDto);

        mvc.perform(get("/api/v1/users/{senderId}/followers", senderId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(followsUserDto.size()))
                .andExpect(jsonPath("$[0].user.id").value(followsUserDto.getFirst().getUser().getId()))
                .andExpect(jsonPath("$[0].requestDate")
                        .value(followsUserDto.getFirst().getRequestDate().format(formatter)));
    }

    @Test
    void testFindAllFriends() throws Exception {
        when(friendsService.findAllFriends(anyLong(), anyInt(), anyInt()))
                .thenReturn(friendsUserDto);

        mvc.perform(get("/api/v1/users/{senderId}/friends", senderId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(friendsUserDto.size()))
                .andExpect(jsonPath("$[0].user.id").value(friendsUserDto.getFirst().getUser().getId()))
                .andExpect(jsonPath("$[0].requestDate")
                        .value(friendsUserDto.getFirst().getRequestDate().format(formatter)))
                .andExpect(jsonPath("$[0].confirmationDate")
                        .value(friendsUserDto.getFirst().getConfirmationDate().format(formatter)));
    }

    @Test
    void testFindRecommendationFriends() throws Exception {
        when(friendsService.findRecommendationFriends(anyLong(), anyInt(), anyInt()))
                .thenReturn(recommendationsUserDto);

        mvc.perform(get("/api/v1/users/{senderId}/friends/recommendations", senderId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(recommendationsUserDto.size()))
                .andExpect(jsonPath("$[0].id").value(recommendationsUserDto.getFirst().getId()))
                .andExpect(jsonPath("$[0].email").value(recommendationsUserDto.getFirst().getEmail()))
                .andExpect(jsonPath("$[0].name").value(recommendationsUserDto.getFirst().getName()))
                .andExpect(jsonPath("$[0].rating").value(recommendationsUserDto.getFirst().getRating()))
                .andExpect(jsonPath("$[0].countFollowers")
                        .value(recommendationsUserDto.getFirst().getCountFollowers()))
                .andExpect(jsonPath("$[0].countFriends")
                        .value(recommendationsUserDto.getFirst().getCountFriends()))
                .andExpect(jsonPath("$[0].commonFriendsCount")
                        .value(recommendationsUserDto.getFirst().getCommonFriendsCount()));

    }
}
