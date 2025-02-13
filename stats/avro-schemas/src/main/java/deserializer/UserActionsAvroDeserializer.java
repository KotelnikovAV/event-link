package deserializer;

import ru.eventlink.stats.avro.UserActionAvro;

public class UserActionsAvroDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    public UserActionsAvroDeserializer() {
        super(UserActionAvro.getClassSchema());
    }
}