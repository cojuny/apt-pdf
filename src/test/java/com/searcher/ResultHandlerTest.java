package com.searcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ResultHandlerTest {
    ResultHandler testResultHandler;

    @Before
    public void testInit() {
        testResultHandler = new ResultHandler();
    }

    @Test
    public void testLockSearch() {
        testResultHandler.lockSearch();
        assertTrue(testResultHandler.isLock());
    }

    @Test
    public void testSetCounter() {
        testResultHandler.setCounter(3);
        assertEquals(3, testResultHandler.counter);
    }
}

