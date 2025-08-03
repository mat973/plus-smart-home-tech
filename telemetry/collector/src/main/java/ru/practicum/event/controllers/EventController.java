package ru.practicum.event.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.exceptions.KafkaSendException;
import ru.practicum.event.mapstruct.HubEventMapper;
import ru.practicum.event.mapstruct.SensorEventMapper;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.concurrent.ExecutionException;

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
        long timestamp = sensorEvent.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                SENSOR_TOPIC,
                null,
                timestamp,
                sensorEvent.getHubId(),
                avro        );

        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка отправления hub event в Kafka. HubId: {}, Error: {}",
                        sensorEvent.getId(), exception.getMessage(), exception);
                throw new KafkaSendException("Отправка sensor event", exception);
            } else {
                log.info("Успешная отправка hub event. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    @PostMapping("/hubs")
    public void createHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        HubEventAvro avro = hubEventMapper.toAvro(hubEvent);
        long timestamp = hubEvent.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                HUB_TOPIC,
                null,
                timestamp,
                hubEvent.getHubId(),
                avro
        );

        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка отправления hub event в Kafka. HubId: {}, Error: {}",
                        hubEvent.getHubId(), exception.getMessage(), exception);
                throw new KafkaSendException("Отправка hub event", exception);
            } else {
                log.info("Успешная отправка hub event. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}


