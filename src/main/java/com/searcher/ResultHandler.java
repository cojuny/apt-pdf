package com.searcher;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ResultHandler implements Runnable {
    private boolean lock = true;
    private final String topic = "queue";
    private final KafkaConsumer<String, String> consumer;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean listening = false; // Indicates if the listener has been started

    public ResultHandler() {
        Properties p = new Properties();
        p.put("bootstrap.servers", "127.0.0.1:9092");
        p.put("group.id", topic);
        p.put("enable.auto.commit", "true");
        p.put("auto.commit.interval.ms", "1000");
        p.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        p.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(p);
        System.out.println("YES");
    }

    public void startListening() {
        if (!listening) {
            listening = true;
            Thread listenerThread = new Thread(this);
            listenerThread.start();
        }
    }

    @Override
    public void run() {
        consumer.subscribe(Collections.singletonList(topic));
        try {
            while (listening && !Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }

    public void stopListening() {
        listening = false; 
    }

    public void lockSearch(){
        setLock(true);
    }
    
    public void unlockSearch() {
        setLock(false);
    }
    
    public boolean isLock() {
        return this.lock;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    private void setLock(boolean newLock) {
        boolean oldLock = this.lock;
        this.lock = newLock;
        pcs.firePropertyChange("lock", oldLock, newLock);
    }
}
