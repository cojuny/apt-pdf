package com.searcher;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.searcher.ControlUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
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
    public void testStartAndStopSearchEngine() throws InterruptedException, IOException {
        ControlUtil.startSearchEngineThread();

        ControlUtil.stopSearchEngineThread();

        Thread.sleep(1000);

        String consoleOutput = outContent.toString();
        assertEquals(true, consoleOutput.contains("Search engine process stopped."));
    }
}
