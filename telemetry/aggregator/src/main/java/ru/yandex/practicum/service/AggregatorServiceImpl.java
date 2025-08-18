package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

public class AggregatorServiceImpl implements AggregatorService {
    private static final String HUB_TOPIC = "telemetry.hubs.v1";
    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";
    private static final String SENSOR_SNAPSHOT_TOPIC = "telemetry.snapshots.v1";
    private Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public void agregate(SensorsSnapshotAvro sensorsSnapshotAvro) {

    }
}
