package ru.practicum.event.model.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.hub.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    private String name;
    private Conditions conditions;
    private Actions actions;
    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
