package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;

@Service
@RequiredArgsConstructor
public class HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    public void handleEvent(HubEventAvro event) {
        var hubId = event.getHubId();
        var payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            handleDeviceAdded(hubId, deviceAdded);
        } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            handleDeviceRemoved(hubId, deviceRemoved);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            handleScenarioAdded(hubId, scenarioAdded);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            handleScenarioRemoved(hubId, scenarioRemoved);
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro device) {
        if (!sensorRepository.existsById(device.getId())) {
            var sensor = new Sensor();
            sensor.setId(device.getId());
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
        }
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro device) {
        sensorRepository.findByIdAndHubId(device.getId(), hubId)
                .ifPresent(sensorRepository::delete);
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro scenarioAdded) {
        var scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(scenarioAdded.getName());
        scenario = scenarioRepository.save(scenario);

        for (ScenarioConditionAvro condAvro : scenarioAdded.getConditions()) {
            var cond = new Condition();
            cond.setType(condAvro.getType().name());
            cond.setOperation(condAvro.getOperation().name());
            cond.setValue(condAvro.getValue() != null ? (Integer) condAvro.getValue() : null);
            cond = conditionRepository.save(cond);

            var sensor = sensorRepository.findByIdAndHubId(condAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new IllegalStateException("Sensor not found for condition"));

            var sc = new ScenarioCondition(new ScenarioConditionId(scenario.getId(), sensor.getId(), cond.getId()),
                    scenario, sensor, cond);
            scenarioConditionRepository.save(sc);
        }

        for (DeviceActionAvro actAvro : scenarioAdded.getActions()) {
            var act = new Action();
            act.setType(actAvro.getType().name());
            act.setValue(actAvro.getValue() != null ? (Integer) actAvro.getValue() : null);
            act = actionRepository.save(act);

            var sensor = sensorRepository.findByIdAndHubId(actAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new IllegalStateException("Sensor not found for action"));

            var sa = new ScenarioAction(new ScenarioActionId(scenario.getId(), sensor.getId(), act.getId()),
                    scenario, sensor, act);
            scenarioActionRepository.save(sa);
        }
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro scenarioRemoved) {
        scenarioRepository.findByHubIdAndName(hubId, scenarioRemoved.getName())
                .ifPresent(scenarioRepository::delete);
    }
}

