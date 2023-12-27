package com.searcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ControlUtilTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testStartAndStopSearchEngine() throws InterruptedException {
        // Start the search engine thread
        ControlUtil.startSearchEngineThread();

        // Sleep for a moment to let the search engine thread start
        Thread.sleep(2000);

        // Stop the search engine thread
        ControlUtil.stopSearchEngineThread();

        // Sleep for a moment to let the stop operation take effect
        Thread.sleep(1000);

        // Assert that the output contains the expected messages
        String consoleOutput = outContent.toString();
        assertEquals(true, consoleOutput.contains("Search engine initialized."));
        assertEquals(true, consoleOutput.contains("Search engine process stopped."));
    }
}
