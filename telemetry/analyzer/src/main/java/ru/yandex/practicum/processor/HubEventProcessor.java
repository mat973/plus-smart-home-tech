package ru.yandex.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final Consumer<String, HubEventAvro> hubEventsConsumer;
    private volatile boolean running = true;

    @Override
    public void run() {
        hubEventsConsumer.subscribe(List.of("telemetry.hubs.v1"));

        try {
            while (running) {
                var records = hubEventsConsumer.poll(Duration.ofMillis(1000));
                records.forEach(record -> {
                    HubEventAvro event = record.value();
                    log.info("Получено событие от хаба [{}]: {}", event.getHubId(), event);

                    // TODO: сохранить устройства/сценарии в БД через репозитории
                    // TODO: при необходимости вызвать сервис для отсылки дальше (gRPC клиент)
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
        hubEventsConsumer.wakeup(); // прервать poll()
    }
}