package ru.yandex.practicum.kafka_cofig;

import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaSensorSnapshotConsumer {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.value-deserializer}")
    private String valueDeserializer;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public Consumer<String, SpecificRecordBase> kafkaConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return new KafkaConsumer<>(config);
    }

    @Bean
    public SensorSnapshotConsumer eventConsumer(Consumer<String, SpecificRecordBase> consumer) {
        return new SensorSnapshotConsumer(consumer);
    }

    public static class SensorSnapshotConsumer {
        private final Consumer<String, SpecificRecordBase> consumer;
        private volatile boolean running = true;

        public SensorSnapshotConsumer(Consumer<String, SpecificRecordBase> consumer) {
            this.consumer = consumer;
        }

        public void subscribe(String topic) {
            consumer.subscribe(Collections.singletonList(topic));

            try {
                while (running) {
                    ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                        processRecord(record);
                    }

                    if (!records.isEmpty()) {
                        consumer.commitSync(); // ручное подтверждение обработки
                    }
                }
            } finally {
                close();
            }
        }

        private void processRecord(ConsumerRecord<String, SpecificRecordBase> record) {
            SpecificRecordBase value = record.value();
            String key = record.key();

            // Здесь ваша логика обработки сообщения
            System.out.printf("Consumed record - key: %s, value: %s, partition: %d, offset: %d%n",
                    key, value, record.partition(), record.offset());
        }

        public void stop() {
            running = false;
        }

        @PreDestroy
        public void close() {
            consumer.close(Duration.ofSeconds(10));
        }
    }
}
