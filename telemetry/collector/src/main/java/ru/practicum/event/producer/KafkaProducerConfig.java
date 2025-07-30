package ru.practicum.event.producer;

import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public Producer<String, SpecificRecordBase> kafkaProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "ru.practicum.event.producer.AvroSerializer");
        return new KafkaProducer<>(config);
    }


    @Bean
    public EventProducer eventProducer(Producer<String, SpecificRecordBase> producer) {
        return new EventProducer(producer);
    }

    public static class EventProducer {
        private final Producer<String, SpecificRecordBase> producer;

        public EventProducer(Producer<String, SpecificRecordBase> producer) {
            this.producer = producer;
        }

        public Producer<String, SpecificRecordBase> getProducer() {
            return producer;
        }

        @PreDestroy
        public void close() {
            producer.close();
        }
    }
}
