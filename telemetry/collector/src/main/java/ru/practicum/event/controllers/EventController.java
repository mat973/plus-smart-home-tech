package ru.practicum.event.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.model.SensorEvent;

@RestController("/events")
public class EventController {
    @PostMapping("/sensor")
    public void createSensorEvent(@Valid @RequestBody SensorEvent sensorEvent){

    }

    @PostMapping("/hubs")
    public void createHubEvent(){

    }

}
