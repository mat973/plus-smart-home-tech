package ru.practicum.event.mapper.hub.mapstruct;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import ru.practicum.event.model.hub.device.DeviceAddedEvent;
import ru.practicum.event.model.hub.device.DeviceRemoveEvent;
import ru.practicum.event.model.hub.scenario.ScenarioAddedEvent;
import ru.practicum.event.model.hub.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.grpc.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface HubProtoToJavaMapper {

    DeviceAddedEvent toJava(DeviceAddedEventProto proto, @Context HubEventProto parent);
    DeviceRemoveEvent toJava(DeviceRemovedEventProto proto, @Context HubEventProto parent);
    ScenarioAddedEvent toJava(ScenarioAddedEventProto proto, @Context HubEventProto parent);
    ScenarioRemovedEvent toJava(ScenarioRemovedEventProto proto, @Context HubEventProto parent);
}

