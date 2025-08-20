package ru.yandex.practicum.runtime;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;

@Component
public class ConsumersRunner implements CommandLineRunner {

    private final Consumer<String, HubEventAvro> hubEventsConsumer;
    private final Consumer<String, SensorsSnapshotAvro> snapshotsConsumer;

    public ConsumersRunner(Consumer<String, HubEventAvro> hubEventsConsumer,
                           Consumer<String, SensorsSnapshotAvro> snapshotsConsumer) {
        this.hubEventsConsumer = hubEventsConsumer;
        this.snapshotsConsumer = snapshotsConsumer;
    }

    @Override
    public void run(String... args) {
        hubEventsConsumer.subscribe(java.util.List.of("hub-events"));
        snapshotsConsumer.subscribe(java.util.List.of("sensors-snapshot"));


        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotsConsumer.poll(Duration.ofMillis(1000));
                records.forEach(r -> {
                    System.out.println("Snapshot: " + r.value().getHubId() + " ts=" + r.value().getTimestamp());
                    // TODO: сохранить в БД
                });
                snapshotsConsumer.commitSync();
            }
        }, "snapshots-thread").start();


        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = hubEventsConsumer.poll(Duration.ofMillis(1000));
                records.forEach(r -> {
                    System.out.println("HubEvent: " + r.value().getHubId() + " payload=" + r.value().getPayload());
                    // TODO: upsert сценариев/устройств в БД
                });

            }
        }, "hub-events-thread").start();
    }
}
