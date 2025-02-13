package ru.eventlink.utility;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class Constants {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ACTUAL_VERSION_EVENT_SERVER = "/api/v1";
    public static final LocalDateTime DEFAULT_SEARCH_START_DATE = LocalDateTime.now().minusYears(20L);
    public static final int MAXIMUM_SIZE_OF_THE_RECOMMENDATION_LIST = 10;
    public static final int DIFFERENCE_RATING_BY_ADD = 1;
    public static final int DIFFERENCE_RATING_BY_DELETE = -1;
    public static final int DIFFERENCE_RATING_BY_UPDATE = 2;
}