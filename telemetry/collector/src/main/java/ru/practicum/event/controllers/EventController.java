package ru.practicum.event.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.exceptions.KafkaSendException;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.SensorEvent;

import ru.practicum.event.mupstruct.HubEventMapper;
import ru.practicum.event.mupstruct.SensorEventMapper;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final Producer<String, SpecificRecordBase> kafkaProducer;
    private final HubEventMapper hubEventMapper;
    private final SensorEventMapper sensorEventMapper;

    private static final String HUB_TOPIC = "telemetry.hubs.v1";
    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";



    @PostMapping("/sensors")
    public void createSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        SensorEventAvro avro = sensorEventMapper.toAvro(sensorEvent);
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(SENSOR_TOPIC, sensorEvent.getHubId(), avro);

        CompletableFuture<Void> future = new CompletableFuture<>();

        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка отправления sensor event в Kafka. HubId: {}, Error: {}",
                        sensorEvent.getHubId(), exception.getMessage(), exception);
                future.completeExceptionally(new KafkaSendException("Ошибка отправления sensor event", exception));
            } else {
                log.info("Успешная отправка sensor event. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
                future.complete(null);
            }
        });
        try {
            future.join();
        } catch (CompletionException e) {
            throw (RuntimeException) e.getCause();
        }

    }

    @PostMapping("/hubs")
    public void createHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        HubEventAvro avro = hubEventMapper.toAvro(hubEvent);
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(HUB_TOPIC, hubEvent.getHubId(), avro);

        CompletableFuture<Void> future = new CompletableFuture<>();

        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка отправления hub event в Kafka. HubId: {}, Error: {}",
                        hubEvent.getHubId(), exception.getMessage(), exception);
                future.completeExceptionally(new KafkaSendException("Ошибка отправления hub event", exception));
            } else {
                log.info("Успешная отправка hub event. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
                future.complete(null);
            }
        });
        try {
            future.join();
        } catch (CompletionException e) {
            throw (RuntimeException) e.getCause();
        }

    }
}
