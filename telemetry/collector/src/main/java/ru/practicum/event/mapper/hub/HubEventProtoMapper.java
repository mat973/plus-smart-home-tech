package ru.practicum.event.mapper.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.hub.device.DeviceAddedEvent;
import ru.practicum.event.model.hub.device.DeviceRemoveEvent;
import ru.practicum.event.model.hub.scenario.ScenarioAddedEvent;
import ru.practicum.event.model.hub.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HubEventProtoMapper {

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    HubEvent toJava(HubEventProto proto);

    default Instant toInstant(com.google.protobuf.Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    @Mapping(target = "payload", source = "payload")
    default HubEvent mapHubEvent(HubEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> deviceAddedToJava(proto);
            case DEVICE_REMOVED -> deviceRemovedToJava(proto);
            case SCENARIO_ADDED -> scenarioAddedToJava(proto);
            case SCENARIO_REMOVED -> scenarioRemovedToJava(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Payload not set");
        };
    }

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    @Mapping(target = "payload.id", source = "payload.deviceAdded.id")
    @Mapping(target = "payload.type", source = "payload.deviceAdded.type")
    DeviceAddedEvent deviceAddedToJava(HubEventProto proto);

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    @Mapping(target = "payload.id", source = "payload.deviceRemoved.id")
    DeviceRemoveEvent deviceRemovedToJava(HubEventProto proto);

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    @Mapping(target = "payload.name", source = "payload.scenarioAdded.name")
    @Mapping(target = "payload.conditions", source = "payload.scenarioAdded.condition")
    @Mapping(target = "payload.actions", source = "payload.scenarioAdded.action")
    ScenarioAddedEvent scenarioAddedToJava(HubEventProto proto);

    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    @Mapping(target = "payload.name", source = "payload.scenarioRemoved.name")
    ScenarioRemovedEvent scenarioRemovedToJava(HubEventProto proto);
}
