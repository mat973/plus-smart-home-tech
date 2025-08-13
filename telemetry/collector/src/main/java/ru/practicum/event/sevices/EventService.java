package ru.practicum.event.sevices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.practicum.event.exceptions.KafkaSendException;
import ru.practicum.event.mapper.hub.mapstruct.HubJavaToAvroMapper;
import ru.practicum.event.mapper.sensor.mapstruct.SensorJavaToAvroMapper;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final SensorJavaToAvroMapper sensorJavaToAvroMapper;
    private final HubJavaToAvroMapper hubJavaToAvroMapper;
    private final Producer<String, SpecificRecordBase> kafkaProducer;

    private static final String HUB_TOPIC = "telemetry.hubs.v1";
    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";


    public void processSensor(SensorEvent sensorEvent) {
        SensorEventAvro avro = sensorJavaToAvroMapper.toAvro(sensorEvent);
        long timestamp = sensorEvent.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                SENSOR_TOPIC,
                null,
                timestamp,
                sensorEvent.getHubId(),
                avro
        );

        sendToKafka(record, sensorEvent.getId());
    }

    public void processHub(HubEvent hubEvent) {
        HubEventAvro avro = hubJavaToAvroMapper.toAvro(hubEvent);
        long timestamp = hubEvent.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                HUB_TOPIC,
                null,
                timestamp,
                hubEvent.getHubId(),
                avro
        );

        sendToKafka(record, hubEvent.getHubId());
    }

    private void sendToKafka(ProducerRecord<String, SpecificRecordBase> record, String keyInfo) {
        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка отправления события в Kafka. Key: {}, Error: {}",
                        keyInfo, exception.getMessage(), exception);
                throw new KafkaSendException("Отправка события", exception);
            } else {
                log.info("Успешная отправка события. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}


