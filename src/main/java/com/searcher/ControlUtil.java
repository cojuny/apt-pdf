package com.searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

public class ControlUtil {

    private static volatile Process searchEngineProcess;
    private static final CountDownLatch latch = new CountDownLatch(2);
    public static int count=1;
    public static void startSearchEngineThread() {
        String[] commands = {
                "source ./SearchEngine/venv/bin/activate",
                "python SearchEngine/src/app.py",
            };

        for (String command : commands) {
            new Thread(() -> {
                try {
                    executeCommand(command);
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

    public static void stopSearchEngineThread() throws IOException {
        if (searchEngineProcess != null) {
            APIClient.sendShutdownSignal();
            searchEngineProcess.destroy();
            System.out.println("Search engine process stopped.");
        } else {
            System.out.println("Search engine process not found.");
        }
    }

    private static void executeCommand(String command) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        processBuilder.redirectErrorStream(true);

        latch.countDown();
        Process process = processBuilder.start();

        searchEngineProcess = process;

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        
        process.waitFor();
    }
}