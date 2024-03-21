package com.searcher;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ResultTest {
    Result testResult;
    String id;
    int startIndex;
    int endIndex;

    @Before
    public void setUp() {
        id = "1";
        startIndex = 10;
        endIndex = 10;
        testResult = new Result(id, startIndex, endIndex);
    }

    @Test
    public void testResult() {
        assertEquals(id, testResult.getId());
        assertEquals(startIndex, testResult.getStartIndex());
        assertEquals(endIndex, testResult.getEndIndex());
    }

    @Test
    public void testProcessDisplayTest() {
        Result case1 = new Result("2", 6, 40);
        String case1text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";

        Result case2 = new Result("3", 16, 21);
        String case2text = "Quick brown fox jumps over the lazy dog.";

        case1.processDisplayText(case1text);
        case2.processDisplayText(case2text);

        assertEquals("{ipsum dolor ...consectetur }", case1.toString());
        assertEquals(" brown fox {jumps} over the l", case2.toString());

    }
}
