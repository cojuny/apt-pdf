package com.searcher;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ResultHandler implements Runnable {

    private int counter = 0;
    private boolean lock = true;
    private final String topic = "queue";
    private final KafkaConsumer<String, String> consumer;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final AtomicBoolean listening = new AtomicBoolean(false); // Indicates if the listener has been started
    

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
        if (!listening.get()) {
            listening.set(true);
            Thread listenerThread = new Thread(this);
            listenerThread.start();
        }
    }

    public void stopListening() {
        listening.set(false);
    }

    @Override
    public void run() {
        consumer.subscribe(Collections.singletonList(topic));
        try {
            System.out.println("Start Listening");
            while (listening.get() && !Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    switch (record.value()) {
                        case "I":
                            lock = false;
                            pcs.firePropertyChange("lock", !lock, lock);
                            break;
                        case "E":
                            counter--;
                            if (counter == 0) {
                                lock = false;
                                pcs.firePropertyChange("lock", !lock, lock);
                            }
                        default:
                            
                            break;
                    }
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }

    public void lockSearch() {
        setLock(true);
    }

    public void unlockSearch() {
        setLock(false);
    }

    public boolean isLock() {
        return this.lock;
    }

    public void setCounter(int val) {
        this.counter = val;
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
