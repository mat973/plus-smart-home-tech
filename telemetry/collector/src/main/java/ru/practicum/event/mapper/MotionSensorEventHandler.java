package ru.practicum.event.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
@Slf4j
public class MotionSensorEventHandler implements SensorEventHandler{
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        MotionSensorProto payload = event.getMotionSensorEvent();
        log.info("Motion sensor: hubId={}, linkQuality={}, motion={}, voltage={}",
                event.getHubId(),
                payload.getLinkQuality(),
                payload.getMotion(),
                payload.getVoltage()
        );
        MotionSensorAvro motionSensorAvro = MotionSensorAvro.newBuilder()
                .
                .build();

    }


}
