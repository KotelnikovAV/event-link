package ru.eventlink.user_actions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import ru.eventlink.stats.avro.ActionTypeAvro;
import ru.eventlink.stats.avro.EventSimilarityAvro;
import ru.eventlink.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;

import static ru.eventlink.user_actions.utility.WeightCoefficients.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorServiceImpl implements AggregatorService {
    private final Map<Long, Map<Long, Double>> matrixWeightsUserAction = new HashMap<>(); // Map<Event, Map<User, Weight>>
    private final Map<Long, Double> sumWeightsEvents = new HashMap<>(); // Map<Event, SUM>
    private final Map<Long, Map<Long, Double>> sumMinimumWeightsEvents = new HashMap<>(); // Map<Event, Map<Event, S_min>>

    @Override
    public List<EventSimilarityAvro> handleRecord(ConsumerRecord<Void, UserActionAvro> record) {
        UserActionAvro userActionAvro = record.value();

        if (matrixWeightsUserAction.containsKey(userActionAvro.getEventId())) {
            Map<Long, Double> userActionWeightsByEventId = matrixWeightsUserAction.get(userActionAvro.getEventId());

            if (userActionWeightsByEventId.get(userActionAvro.getUserId()) == null ||
                    userActionWeightsByEventId.get(userActionAvro.getUserId()) < getWeight(userActionAvro.getActionType())) {
                return recalculateSimilarity(userActionAvro);
            } else {
                return List.of();
            }

        } else if (matrixWeightsUserAction.isEmpty()) {
            putNewEvent(userActionAvro, false);
            return List.of();
        } else {
            putNewEvent(userActionAvro, true);
            return recalculateSimilarity(userActionAvro);
        }
    }

    private double getWeight(ActionTypeAvro actionTypeAvro) {
        switch (actionTypeAvro) {
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

    private void putNewEvent(UserActionAvro userActionAvro, boolean necessityRecalculation) {
        if (necessityRecalculation) {
            matrixWeightsUserAction.computeIfAbsent(userActionAvro.getEventId(), k -> new HashMap<>())
                    .put(userActionAvro.getUserId(), ABSENCE_OF_ACTIONS_RATIO);
            sumWeightsEvents.put(userActionAvro.getEventId(), ABSENCE_OF_ACTIONS_RATIO);
        } else {
            matrixWeightsUserAction.computeIfAbsent(userActionAvro.getEventId(), k -> new HashMap<>())
                    .put(userActionAvro.getUserId(), getWeight(userActionAvro.getActionType()));
            sumWeightsEvents.put(userActionAvro.getEventId(), getWeight(userActionAvro.getActionType()));
        }
    }

    private List<Long> getEventsByUserId(Long userId, Long eventId) {
        List<Long> events = new ArrayList<>();
        Set<Long> eventsId = matrixWeightsUserAction.keySet();

        for (Long event : eventsId) {
            Map<Long, Double> userActionWeights = matrixWeightsUserAction.get(event);

            if (userActionWeights.containsKey(userId) && !event.equals(eventId)) {
                events.add(event);
            }
        }

        return events;
    }

    private List<EventSimilarityAvro> recalculateSimilarity(UserActionAvro userActionAvro) {
        List<EventSimilarityAvro> eventsSimilarityAvro = new ArrayList<>();
        Long userId = userActionAvro.getUserId();
        Long eventId = userActionAvro.getEventId();
        ActionTypeAvro actionType = userActionAvro.getActionType();
        Map<Long, Double> userActionsWeightsByEventId = matrixWeightsUserAction.get(eventId);

        List<Long> userInteractionEvents = getEventsByUserId(userId, eventId);

        if (userInteractionEvents.isEmpty()) {
            userActionsWeightsByEventId.replace(userId, getWeight(actionType));
            sumWeightsEvents.put(eventId, getWeight(actionType));
            return List.of();
        }

        for (Long event : userInteractionEvents) {
            long firstEventId = Math.min(event, eventId);
            long secondEventId = Math.max(event, eventId);
            Map<Long, Double> userActionWeights = matrixWeightsUserAction.get(event);
            double deltaContribution = Math.min(getWeight(actionType), userActionWeights.get(userId)) -
                    Math.min(userActionsWeightsByEventId.getOrDefault(userId, 0.0), userActionWeights.get(userId));
            double deltaOldNewWeight = getWeight(actionType) - userActionsWeightsByEventId.getOrDefault(userId, 0.0);

            if (deltaContribution != 0.0) {
                if (!sumMinimumWeightsEvents.containsKey(firstEventId)) {
                    calculateSumMin(firstEventId, secondEventId);
                }

                Map<Long, Double> sumMinimumWeightsEvent = sumMinimumWeightsEvents.get(firstEventId);
                sumMinimumWeightsEvent
                        .replace(secondEventId, sumMinimumWeightsEvent.get(secondEventId) + deltaContribution);
            }

            double summarily = calculateSimilarity(eventId, firstEventId, secondEventId, deltaOldNewWeight);
            eventsSimilarityAvro.add(getEventSimilarity(firstEventId, secondEventId, summarily));
        }

        if (userActionsWeightsByEventId.containsKey(userId)) {
            userActionsWeightsByEventId.replace(userId, getWeight(actionType));
        } else {
            userActionsWeightsByEventId.put(eventId, getWeight(actionType));
        }

        return eventsSimilarityAvro;
    }

    private EventSimilarityAvro getEventSimilarity(long firstEventId, long secondEventId, double score) {
        return EventSimilarityAvro.newBuilder()
                .setEventA(firstEventId)
                .setEventB(secondEventId)
                .setScore(score)
                .setTimestamp(Instant.now())
                .build();
    }

    private Double calculateSimilarity(long eventId, long firstEventId, long secondEventId, double deltaOldNewWeight) {
        double newSumWeights = sumWeightsEvents.getOrDefault(eventId, 0.0) + deltaOldNewWeight;
        double sumMin = sumMinimumWeightsEvents
                .computeIfAbsent(firstEventId, e -> new HashMap<>()).getOrDefault(secondEventId, 0.0);
        sumWeightsEvents.replace(eventId, newSumWeights);

        return sumMin / (Math.sqrt(newSumWeights) + Math.sqrt(sumWeightsEvents.get(secondEventId)));
    }

    private void calculateSumMin(long firstEventId, long secondEventId) {
        Map<Long, Double> userActionWeightsByFirstEventId = matrixWeightsUserAction.get(firstEventId);
        Map<Long, Double> userActionWeightsBySecondEventId = matrixWeightsUserAction.get(secondEventId);
        long maxUserIdFromUserActionWeightsByFirstEventId = userActionWeightsByFirstEventId.keySet()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);
        long maxUserIdFromUserActionWeightsBySecondEventId = userActionWeightsBySecondEventId.keySet()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);
        long maxUserId = Math
                .max(maxUserIdFromUserActionWeightsByFirstEventId, maxUserIdFromUserActionWeightsBySecondEventId);
        double minSum = 0.0;

        for (long i = 0; i <= maxUserId; i++) {
            minSum = minSum + Math.min(userActionWeightsByFirstEventId.getOrDefault(i, 0.0),
                    userActionWeightsBySecondEventId.getOrDefault(i, 0.0));
        }

        Map<Long, Double> newMinSum = new HashMap<>();
        newMinSum.put(secondEventId, minSum);
        sumMinimumWeightsEvents.put(firstEventId, newMinSum);
    }
}
