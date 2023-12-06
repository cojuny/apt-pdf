package com.searcher;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HelloWorldTest {
    @Test
    public void testPrintHelloWorld() {
        HelloWorld app = new HelloWorld();
        assertEquals("Hello from Java", app.hello_world());
    }
}
