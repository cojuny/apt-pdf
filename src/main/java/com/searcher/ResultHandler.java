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
import java.util.List;

public class ResultHandler implements Runnable {

    protected int counter = 0;
    protected boolean lock = true;
    protected boolean newResult = false;
    private final String topic = "queue";
    protected List<Result> fullResults = new ArrayList<>();;
    private final KafkaConsumer<String, String> consumer;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final AtomicBoolean listening = new AtomicBoolean(false); 

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
                            pcs.firePropertyChange("lock", true, false);
                            break;
                        case "E":
                            counter--;
                            if (counter == 0) {
                                lock = false;
                                pcs.firePropertyChange("lock", true, false);
                            }
                            break;
                        default:
                            String[] values = record.value().split("/");
                            Result result = new Result(record.key(), Integer.parseInt(values[0]),
                                    Integer.parseInt(values[1]));
                            fullResults.add(result);
                            PDFManager.documents.get(PDFManager.idToIndex(record.key())).results.add(result);
                            pcs.firePropertyChange("newResult", false, true);
                            break;
                    }
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(),
                            record.value());
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
