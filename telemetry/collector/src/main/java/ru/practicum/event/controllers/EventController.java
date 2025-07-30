package ru.practicum.event.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.SensorEvent;

import ru.practicum.event.mupstruct.HubEventMapper;
import ru.practicum.event.mupstruct.SensorEventMapper;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;


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
        kafkaProducer.send(record);
    }

    @PostMapping("/hubs")
    public void createHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        HubEventAvro avro = hubEventMapper.toAvro(hubEvent);
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(HUB_TOPIC, hubEvent.getHubId(), avro);
        kafkaProducer.send(record);
    }
}
