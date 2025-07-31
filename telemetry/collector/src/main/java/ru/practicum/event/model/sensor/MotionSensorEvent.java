package ru.practicum.event.model.sensor;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.SensorEvent;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {
    @NotBlank(message = "Качество связи не может быть пустым")
    private Integer linkQuality;
    @NotBlank(message = "Наличие/отсутствие движения не может быть пустым")
    private boolean motion;
    @NotBlank(message = "Напряжение не может быть пустым")
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
