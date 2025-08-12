package ru.practicum.event.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.event.model.sensor.*;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProtoToJavaMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    ClimateSensorEvent climateToJava(SensorEventProto proto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    LightSensorEvent lightToJava(SensorEventProto proto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    MotionSensorEvent motionToJava(SensorEventProto proto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    SwitchSensorEvent switchToJava(SensorEventProto proto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    TemperatureSensorEvent temperatureToJava(SensorEventProto proto);

    default Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}

