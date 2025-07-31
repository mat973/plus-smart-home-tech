package ru.practicum.event.producer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final KafkaProducerConfig.EventProducer eventProducer;

    public EventService(KafkaProducerConfig.EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    public void sendEvent(String topic, SpecificRecordBase event) {
        eventProducer.getProducer().send(new ProducerRecord<>(topic, event));
    }
}
