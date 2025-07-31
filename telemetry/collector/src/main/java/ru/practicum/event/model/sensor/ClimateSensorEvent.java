package ru.practicum.event.model.sensor;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.SensorEvent;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {
    @NotBlank(message = "Уровень температуры по шкале Цельсия не омжет быть пустым")
    private Integer temperatureC;
    @NotBlank(message = "Влажность не может быть пустая")
    private Integer humidity;
    @NotBlank(message = "Уровень CO2 не может быть пустым")
    private Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
