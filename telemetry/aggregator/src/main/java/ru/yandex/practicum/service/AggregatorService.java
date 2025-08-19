package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface AggregatorService {
    void agregate(SensorEventAvro sensorEventAvro);
}
