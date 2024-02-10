package com.searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

public class ControlUtil {

    private static volatile Process searchEngineProcess, zooKeeperProcess, kafkaProcess;
    public static void startSearchEngineThread() {
        String[] commands = {
                "source ./SearchEngine/venv/bin/activate",
                "python SearchEngine/src/APIServer.py",
            };
        startProcess(commands, 0);
    }

    public static void stopSearchEngineThread() throws IOException {
        if (searchEngineProcess != null) {
            APIClient.sendShutdownSignal();
            searchEngineProcess.destroy();
            System.out.println("Search engine process stopped.");
        } else {
            System.out.println("Search engine process not found.");
        }
    }

    public static void startResultQueueThread() throws Exception {
        String home = "./src/main/resources/kafka";
        String[] commands = {
            home + "/bin/zookeeper-server-start.sh " + home + "/config/zookeeper.properties &",
        };

        startProcess(commands, 1);
        Thread.sleep(20000); //https://github.com/wurstmeister/kafka-docker/issues/389
        
        commands = new String[]{
            home + "/bin/kafka-server-start.sh " + home + "/config/server.properties &"
        };

        startProcess(commands, 2); 
    }


    public static void stopResultQueueThread() throws Exception {
        if (kafkaProcess != null) {
            stopProcess(2);
            kafkaProcess.destroy();
        } else {
            System.out.println("Kafka process not found.");
        }
        
        if (zooKeeperProcess != null) {
            stopProcess(1);
            zooKeeperProcess.destroy();

        } else {
            System.out.println("ZooKeeper process not found.");
        }
        
    }

    private static void startProcess(String[] commands, int type) {
        CountDownLatch latch = new CountDownLatch(commands.length);

        for (String command : commands) {
            new Thread(() -> {
                try {
                    executeCommand(command, latch, type);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            latch.await();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Latch interrupted.");
        }
    }

    private static void stopProcess(int type) throws Exception{
        if (type == 1) {
            executeCommand("kill $(ps aux | grep '[z]ookeeper' | awk '{print $2}')", null, type);
        } else {
            executeCommand("kill $(ps aux | grep '[k]afka' | awk '{print $2}')", null, type);
        }
    }

    private static void executeCommand(String command, CountDownLatch latch, int type) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        processBuilder.redirectErrorStream(true);

        if (latch != null) {
            latch.countDown();
        }
        Process process = processBuilder.start();
        
        switch (type) {
            case 0:
                searchEngineProcess = process;
                break;
            case 1:
                zooKeeperProcess = process;
                break;
            case 2:
                kafkaProcess = process;
                break;
        }

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        process.waitFor();
    }
}