package ru.practicum.event.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.hub.HubEventType;

import java.time.Instant;

@Getter
@Setter
@ToString
public abstract class HubEvent {
    private String hubId;
    private Instant timestamp = Instant.now();

    public abstract HubEventType getType();
}
