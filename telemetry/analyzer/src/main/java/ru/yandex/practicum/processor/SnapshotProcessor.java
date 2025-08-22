package ru.yandex.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.evaluator.ScenarioEvaluator;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    @Value("${topic.snapshots}")
    private String snapshotsTopic;
    private final ScenarioEvaluator scenarioEvaluator;
    private final Consumer<String, SensorsSnapshotAvro> snapshotsConsumer;
    private volatile boolean running = true;

    public void start() {
        snapshotsConsumer.subscribe(List.of(snapshotsTopic));

        try {
            while (running) {
                var records = snapshotsConsumer.poll(Duration.ofMillis(1000));
                for (var record : records) {
                    try {
                        SensorsSnapshotAvro snapshot = record.value();
                        log.info("Получен снапшот от хаба [{}]: {}", snapshot.getHubId(), snapshot);

                        scenarioEvaluator.evaluateSnapshot(snapshot);

                    } catch (Exception e) {
                        log.error("Ошибка при обработке снапшота: {}", record, e);
                    }
                }
                if (!records.isEmpty()) {
                    snapshotsConsumer.commitSync();
                }
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

