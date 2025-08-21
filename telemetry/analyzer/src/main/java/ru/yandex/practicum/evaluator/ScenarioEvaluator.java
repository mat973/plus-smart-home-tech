package ru.yandex.practicum.evaluator;

import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScenarioEvaluator {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final EvaluatorRegistry registry;
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void evaluate(SensorsSnapshotAvro snapshot) {
        // Загружаем сценарии для конкретного hubId
        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());

        scenarios.stream()
                .filter(scenario -> checkAllConditions(scenario, snapshot))
                .forEach(scenario -> executeActions(scenario, snapshot));
    }

    /**
     * Проверяем все условия сценария
     */
    private boolean checkAllConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        return scenario.getConditions().stream().allMatch(cond -> {
            // Находим состояние сенсора по id
            Sensor sensor = sensorRepository.findByIdAndHubId(cond.getId().toString(), scenario.getHubId())
                    .orElse(null);
            if (sensor == null) return false;

            SensorStateAvro sensorState = snapshot.getSensorsState().get(sensor.getId());
            if (sensorState == null) return false;

            ConditionType type = ConditionType.valueOf(cond.getType());
            Operation op = Operation.valueOf(cond.getOperation());

            return registry.extractValue(type, sensorState)
                    .map(actual -> registry.compare(op, actual, BigDecimal.valueOf(cond.getValue())))
                    .orElse(false);
        });
    }

    /**
     * Выполняем действия сценария (отправляем gRPC команды)
     */
    private void executeActions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        scenario.getActions().forEach(act -> {
            ActionType type = ActionType.valueOf(act.getType());

            DeviceActionProto proto = registry.buildAction(
                    type,
                    act.getId().toString(),   // ⚠️ тут лучше будет связка через sensor_id из связующей таблицы
                    Optional.ofNullable(act.getValue()).orElse(0)
            );

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(snapshot.getHubId())
                    .setScenarioName(scenario.getName())
                    .setAction(proto)
                    .setTimestamp(Timestamps.fromMillis(snapshot.getTimestamp().getEpochSecond()))
                    .build();

            hubRouterClient.handleDeviceAction(request);
        });
    }
}

