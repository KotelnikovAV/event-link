package ru.eventlink.user_action.service;

import ru.eventlink.stats.avro.UserActionAvro;
import ru.eventlink.user_action.model.ActionType;

import java.util.List;

import static ru.eventlink.utility.Constants.*;

public interface UserActionService {
    void saveUserActions(List<UserActionAvro> userActionsAvro);

    default double getWeight(ActionType actionType) {
        switch (actionType) {
            case VIEW -> {
                return VIEWING_RATIO;
            }
            case REGISTER -> {
                return REGISTRATION_RATIO;
            }
            case LIKE -> {
                return LIKE_RATIO;
            }
            default -> {
                return 0.0;
            }
        }
    }
}
