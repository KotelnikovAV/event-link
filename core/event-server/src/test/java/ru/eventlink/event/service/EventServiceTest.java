//package ru.eventlink.event.service;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import ru.eventlink.category.model.Category;
//import ru.eventlink.category.repository.CategoryRepository;
//import ru.eventlink.client.RecommendationsClient;
//import ru.eventlink.client.StatClient;
//import ru.eventlink.client.UserActionClient;
//import ru.eventlink.client.requests.RequestClient;
//import ru.eventlink.client.user.UserClient;
//import ru.eventlink.dto.event.EventFullDto;
//import ru.eventlink.enums.State;
//import ru.eventlink.event.model.Event;
//import ru.eventlink.event.model.Location;
//import ru.eventlink.event.repository.EventRepository;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.notNullValue;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//public class EventServiceTest {
//
//    @Autowired
//    private EventService eventService;
//
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @MockBean
//    private StatClient statClient;
//
//    @MockBean
//    private UserClient userClient;
//
//    @MockBean
//    private RequestClient requestClient;
//
//    @MockBean
//    private RecommendationsClient recommendationsClient;
//
//    @MockBean
//    private UserActionClient userActionClient;
//
//    private static List<Event> events;
//    private static Category category;
//
//    @BeforeAll
//    public static void setUp() {
//        category = new Category();
//        category.setName("Category");
//
//        events = new ArrayList<>();
//
//        for (int i = 0; i < 100; i++) {
//            Location location = new Location();
//            location.setLat((float) i);
//            location.setLon((float) i);
//
//            Event event = new Event();
//            event.setAnnotation("event" + i);
//            event.setCategory(category);
//            event.setCreatedOn(LocalDateTime.now());
//            event.setDescription("description" + i);
//            event.setEventDate(LocalDateTime.now().plusMonths(1L));
//            event.setInitiatorId((long) i);
//            event.setLocation(location);
//            event.setPaid(true);
//            event.setParticipantLimit(1L);
//            event.setPublishedOn(LocalDateTime.now());
//            event.setRequestModeration(true);
//            event.setState(State.PUBLISHED);
//            event.setTitle("title" + i);
//            event.setConfirmedRequests(0L);
//            event.setLikes(0L);
//            events.add(event);
//        }
//
//    }
//
//    @Test
//    public void setEventService() {
//        categoryRepository.save(category);
//        eventRepository.saveAll(events);
//    }
//
//    @Test
//    public void test() throws InterruptedException {
//
//        eventRepository.saveAll(events);
//
//        List<EventFullDto> events = eventService.findAllAdminEvents(List.of(1L, 2L, 3L),
//                State.PUBLISHED, null,
//                null,
//                null,
//                0,
//                10,
//                false);
//
//        System.out.println(events);
//        assertThat(events, notNullValue());
//    }
//
//    @Test
//    public void testDeleteAll() {
//        eventRepository.deleteAll();
//        categoryRepository.deleteAll();
//    }
//
//
//}
