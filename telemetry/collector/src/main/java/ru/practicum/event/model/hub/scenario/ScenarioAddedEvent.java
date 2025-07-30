package ru.practicum.event.model.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.hub.HubEventType;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    private String name;
    private List<Conditions> conditions;
    private List<Actions> actions;
    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
