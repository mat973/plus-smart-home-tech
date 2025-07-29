package ru.practicum.event.model.hub.device;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.hub.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemoveEvent extends HubEvent {
    private String id;
    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
