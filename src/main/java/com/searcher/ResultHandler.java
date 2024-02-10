package com.searcher;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class ResultHandler implements Runnable {
    private boolean lock = true;
    private String topic = "queue";
    private KafkaConsumer<String, String> consumer;

    public ResultHandler() {
        Properties p = new Properties();
        p.put("bootstrap.servers", "127.0.0.1:9092");
        p.put("group.id", topic);
        p.put("enable.auto.commit", "true");
        p.put("auto.commit.interval.ms", "1000");
        p.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        p.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(p);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList(topic));
            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }
    
    public void lockSearch(){
        this.lock = true;
    }
    private void unlockSearch() {
        this.lock = false;
    }
    public boolean isLock() {
        return this.lock;
    }
}