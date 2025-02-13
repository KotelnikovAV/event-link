package ru.eventlink.user_actions.utility;

import lombok.experimental.UtilityClass;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

@UtilityClass
public class ProducerActivityTimer {
    private static Thread threadForTrackingProducerActivity;

    public void stopActivityTimerProducer() {
        if (threadForTrackingProducerActivity != null && threadForTrackingProducerActivity.isAlive()) {
            threadForTrackingProducerActivity.interrupt();
            try {
                threadForTrackingProducerActivity.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void startActivityTimerProducer(Producer<String, SpecificRecordBase> producer,
                                           long timeMs,
                                           Object monitor) {
        stopActivityTimerProducer();

        threadForTrackingProducerActivity = new Thread(() -> {
            try {
                Thread.sleep(timeMs);

                synchronized (monitor) {
                    producer.flush();
                    producer.close();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        threadForTrackingProducerActivity.start();
    }

    public boolean getNecessityNewProducer() {
        return threadForTrackingProducerActivity == null || !threadForTrackingProducerActivity.isAlive();
    }
}