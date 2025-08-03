package ru.practicum.event.model.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.sensor.SensorType;

@Getter
@Setter
@ToString
public class Conditions {
    private String sensorId;
    private SensorType type;
    private OperationType operation;
    private Integer value;
}
