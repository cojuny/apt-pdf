package com.searcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ControlUtilTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testStartAndStopSearchEngine() throws Exception {
        ControlUtil.startSearchEngineThread();

        ControlUtil.stopSearchEngineThread();

        Thread.sleep(1000);

        String consoleOutput = outContent.toString();
        assertEquals(true, consoleOutput.contains("Search engine process stopped."));
    } 

    @Test
    public void testStartAndStopResultQueue() throws Exception {
        ControlUtil.startResultQueueThread();

        ControlUtil.stopResultQueueThread();

        Thread.sleep(1000);

        String consoleOutput = outContent.toString();
        System.out.println(consoleOutput);
        assertEquals(true, consoleOutput.contains("Kafka process stopped."));
        consoleOutput = outContent.toString();
        assertEquals(true, consoleOutput.contains("ZooKeeper process stopped."));
    } 

    @Test
    public void testStopResultQueueNoProcess() throws Exception {
        ControlUtil.stopResultQueueThread();
        String consoleOutput = outContent.toString();
        System.out.println(consoleOutput);
        assertEquals(true, consoleOutput.contains("Kafka process not found."));
        assertEquals(true, consoleOutput.contains("ZooKeeper process not found."));
    }

    @Test
    public void testStopSearchEngineNoProcess() throws Exception {
        ControlUtil.stopSearchEngineThread();
        String consoleOutput = outContent.toString();
        System.out.println(consoleOutput);
        assertEquals(true, consoleOutput.contains("Search engine process not found."));
    }
}
