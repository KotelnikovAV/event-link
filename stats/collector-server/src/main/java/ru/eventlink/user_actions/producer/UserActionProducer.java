package ru.eventlink.user_actions.producer;

import ru.eventlink.stats.proto.UserActionProto;

public interface UserActionProducer {
    void collectUserAction(UserActionProto userActionProto);
}
