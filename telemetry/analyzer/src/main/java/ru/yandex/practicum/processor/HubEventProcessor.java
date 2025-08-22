package ru.yandex.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubEventService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    @Value("${topic.hub-events}")
    private String hubEventsTopic;
    private final Consumer<String, HubEventAvro> hubEventsConsumer;
    private final HubEventService hubEventService;
    private volatile boolean running = true;

    @Override
    public void run() {
        hubEventsConsumer.subscribe(List.of(hubEventsTopic));

        try {
            while (running) {
                var records = hubEventsConsumer.poll(Duration.ofMillis(1000));

                records.forEach(record -> {
                    HubEventAvro event = record.value();
                    log.info("Получено событие от хаба [{}]: {}", event.getHubId(), event);

                    hubEventService.handleEvent(event);
                });
            }
        } catch (WakeupException e) {
            log.info("HubEventProcessor wakeup: {}", e.getMessage());
        } finally {
            hubEventsConsumer.close();
            log.info("HubEventProcessor корректно завершён");
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Останавливаем HubEventProcessor...");
        running = false;
        hubEventsConsumer.wakeup();
    }
}
