package com.searcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ControlUtil {
    
    private static volatile Process searchEngineProcess;

    public static void startSearchEngineThread() {
        String[] commands = {
            "source ./SearchEngine/venv/bin/activate",
            "python SearchEngine/src/app.py",
            "echo 'Search engine initialized.'"
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
        Process process = processBuilder.start();

        // Store the reference to the process
        searchEngineProcess = process;

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Exit Code: " + exitCode);
    }
}
