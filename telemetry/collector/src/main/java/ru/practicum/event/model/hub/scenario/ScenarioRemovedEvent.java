package ru.practicum.event.model.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.hub.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    private String name;
    private HubEventType type;
    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
