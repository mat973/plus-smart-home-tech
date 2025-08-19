package ru.yandex.practicum.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AggregatorServiceImpl implements AggregatorService {
    private static final String HUB_TOPIC = "telemetry.hubs.v1";
    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";
    private static final String SENSOR_SNAPSHOT_TOPIC = "telemetry.snapshots.v1";
    private Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    private final Producer<String, SpecificRecordBase> producer;

    public AggregatorServiceImpl(Producer<String, SpecificRecordBase> producer) {
        this.producer = producer;
    }

    @Override
    public void agregate(SensorEventAvro event) {

    }


    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro sensorsSnapshotAvro = snapshots.computeIfAbsent(event.getHubId(), hubId -> {
            SensorsSnapshotAvro newSnapshot = new SensorsSnapshotAvro();
            newSnapshot.setHubId(hubId);
            newSnapshot.setSensorsState(new HashMap<>());
            return newSnapshot;
        });
        if (sensorsSnapshotAvro.getSensorsState().containsKey(event.getId())) {
             SensorStateAvro oldState = sensorsSnapshotAvro.getSensorsState().get(event.getId());
             if (event.getTimestamp().isBefore(oldState.getTimestamp()) ||
                     oldState.getData().equals(event.getPayload())){
                 return Optional.empty();
             }
        }
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
        sensorsSnapshotAvro.getSensorsState().put(event.getId(), newState);
        sensorsSnapshotAvro.setTimestamp(event.getTimestamp());
        snapshots.put(sensorsSnapshotAvro.getHubId(), sensorsSnapshotAvro);

        return Optional.of(sensorsSnapshotAvro);
    }
}
