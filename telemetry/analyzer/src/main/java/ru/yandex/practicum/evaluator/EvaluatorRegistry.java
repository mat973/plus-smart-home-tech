package ru.yandex.practicum.evaluator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.model.Operation;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Component
public class EvaluatorRegistry {

    private final Map<ConditionType, Function<SensorStateAvro, Optional<BigDecimal>>> extractors =
            Map.of(
                    ConditionType.TEMPERATURE, st -> {
                        Object data = st.getData();
                        if (data instanceof TemperatureSensorAvro temp) {
                            return Optional.of(BigDecimal.valueOf(temp.getTemperatureC()));
                        }
                        if (data instanceof ClimateSensorAvro climate) {
                            return Optional.of(BigDecimal.valueOf(climate.getTemperatureC()));
                        }
                        return Optional.empty();
                    },
                    ConditionType.HUMIDITY, st -> {
                        if (st.getData() instanceof ClimateSensorAvro climate) {
                            return Optional.of(BigDecimal.valueOf(climate.getHumidity()));
                        }
                        return Optional.empty();
                    },
                    ConditionType.CO2LEVEL, st -> {
                        if (st.getData() instanceof ClimateSensorAvro climate) {
                            return Optional.of(BigDecimal.valueOf(climate.getCo2Level()));
                        }
                        return Optional.empty();
                    },
                    ConditionType.LUMINOSITY, st -> {
                        if (st.getData() instanceof LightSensorAvro light) {
                            return Optional.of(BigDecimal.valueOf(light.getLuminosity()));
                        }
                        return Optional.empty();
                    },
                    ConditionType.MOTION, st -> {
                        if (st.getData() instanceof MotionSensorAvro motion) {
                            return Optional.of(motion.getMotion() ? BigDecimal.ONE : BigDecimal.ZERO);
                        }
                        return Optional.empty();
                    },
                    ConditionType.SWITCH, st -> {
                        if (st.getData() instanceof SwitchSensorAvro sw) {
                            return Optional.of(sw.getState() ? BigDecimal.ONE : BigDecimal.ZERO);
                        }
                        return Optional.empty();
                    },
                    ConditionType.UNKNOWN, st -> Optional.empty()
            );

    private final Map<Operation, BiPredicate<BigDecimal, BigDecimal>> comparators =
            Map.of(
                    Operation.GREATER_THAN, (a, b) -> a.compareTo(b) > 0,
                    Operation.LOWER_THAN,   (a, b) -> a.compareTo(b) < 0,
                    Operation.EQUALS,       BigDecimal::equals
            );

    // Как собирать gRPC действие
    private final Map<ActionType, BiFunction<String, Integer, DeviceActionProto>> actionBuilders =
            Map.of(
                    ActionType.SET_VALUE,  (sensorId, v) -> DeviceActionProto.newBuilder()
                            .setSensorId(sensorId)
                            .setType(ActionTypeProto.SET_VALUE)
                            .setValue(v).build(),
                    ActionType.ACTIVATE,   (sensorId, v) -> DeviceActionProto.newBuilder()
                            .setSensorId(sensorId)
                            .setType(ActionTypeProto.ACTIVATE)
                            .setValue(1).build(),
                    ActionType.DEACTIVATE, (sensorId, v) -> DeviceActionProto.newBuilder()
                            .setSensorId(sensorId)
                            .setType(ActionTypeProto.DEACTIVATE)
                            .setValue(0).build(),
                    ActionType.INVERSE,    (sensorId, v) -> DeviceActionProto.newBuilder()
                            .setSensorId(sensorId)
                            .setType(ActionTypeProto.INVERSE)
                            .build(),
                    ActionType.NOOP,       (sensorId, v) -> DeviceActionProto.newBuilder()
                            .setSensorId(sensorId)
                            .setType(ActionTypeProto.UNRECOGNIZED)
                            .build()
            );

    public Optional<BigDecimal> extractValue(ConditionType type, SensorStateAvro state) {
        return extractors.getOrDefault(type, st -> Optional.empty()).apply(state);
    }

    public boolean compare(Operation op, BigDecimal left, BigDecimal right) {
        return comparators.getOrDefault(op, (a, b) -> false).test(left, right);
    }

    public DeviceActionProto buildAction(ActionType type, String sensorId, Integer value) {
        return actionBuilders.getOrDefault(type, (id, v) -> DeviceActionProto.getDefaultInstance())
                .apply(sensorId, value);
    }
}

