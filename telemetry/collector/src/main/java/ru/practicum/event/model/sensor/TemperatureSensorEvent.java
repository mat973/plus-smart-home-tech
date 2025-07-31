package ru.practicum.event.model.sensor;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.SensorEvent;

@Getter
@Setter
@ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {
    @NotBlank(message = "Температура в градусах Цельсия не может быть пустой")
    private Integer temperatureC;
    @NotBlank(message = "Температура в градусах Фаренгейта не может быть пустой")
    private Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
