package ru.yandex.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final ScenarioEvaluator scenarioEvaluator;
    private final Consumer<String, SensorsSnapshotAvro> snapshotsConsumer;
    private volatile boolean running = true;

    public void start() {
        snapshotsConsumer.subscribe(List.of("telemetry.snapshots.v1"));

        try {
            while (running) {
                var records = snapshotsConsumer.poll(Duration.ofMillis(1000));

                records.forEach(record -> {
                    SensorsSnapshotAvro snapshot = record.value();
                    log.info("Получен снапшот от хаба [{}]: {}", snapshot.getHubId(), snapshot);

                    // Оцениваем сценарии и выполняем действия
                    scenarioEvaluator.evaluateSnapshot(snapshot);
                });

                snapshotsConsumer.commitSync(); // ручной коммит смещений
            }
        } catch (WakeupException e) {
            log.info("SnapshotProcessor wakeup: {}", e.getMessage());
        } finally {
            snapshotsConsumer.close();
            log.info("SnapshotProcessor корректно завершён");
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Останавливаем SnapshotProcessor...");
        running = false;
        snapshotsConsumer.wakeup();
    }
}

