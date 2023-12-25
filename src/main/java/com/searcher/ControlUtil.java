package com.searcher;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ControlUtil {
    
    public static void startSearchEngine() throws Exception {
        // Specify the path to your Bash script
        String scriptPath = "src/main/resources/StartSearchEngine.sh";

        // Create process builder
        ProcessBuilder processBuilder = new ProcessBuilder("bash", scriptPath);

        // Redirect error stream to output stream
        processBuilder.redirectErrorStream(true);

        // Start the process
        Process process = processBuilder.start();

        // Capture the output
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        // Read and print each line of the output
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Wait for the process to finish
        int exitCode = process.waitFor();
        System.out.println("Exit Code: " + exitCode);
    }
}
