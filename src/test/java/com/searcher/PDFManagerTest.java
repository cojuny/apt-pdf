package com.searcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class PDFManagerTest {

    private PDFManager testPDFManager;
    private final String FILEPATH = "src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf";

    @Before
    public void setUp() throws Exception {
        
        try (MockedStatic<Executors> mockedExecutor = Mockito.mockStatic(Executors.class)) {
            mockedExecutor.when(() -> Executors.newFixedThreadPool(anyInt()))
                .thenReturn(null);
        }
        try (MockedStatic<CompletableFuture> mockedProcess = Mockito.mockStatic(CompletableFuture.class)) {
            mockedProcess.when(() -> CompletableFuture.runAsync(any()))
                .thenReturn(null);
        }
        try (MockedStatic<APIClient> mockedApiClient = Mockito.mockStatic(APIClient.class)) {
            mockedApiClient.when(() -> APIClient.sendText(anyString(), anyString()))
                .thenReturn("Response Status: 200");
            mockedApiClient.when(() -> APIClient.sendDelete(anyString()))
                .thenReturn("Response Status: 200");
            mockedApiClient.when(() -> APIClient.sendSearchLexical(anyString(), any(), any(), anyString()))
                .thenReturn("Lexical search successful");
            mockedApiClient.when(() -> APIClient.sendSearchKeyword(anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn("Keyword search successful");
            mockedApiClient.when(() -> APIClient.sendSearchSemantic(anyString(), anyString(), anyInt()))
                .thenReturn("Semantic search successful");
            mockedApiClient.when(() -> APIClient.sendShutdownSignal())
                .thenReturn("Response Status: 200");

        }

        testPDFManager = new PDFManager();
    }

    @Test
    public void testOpenDocument() throws IOException {
        boolean result = testPDFManager.openDocument(FILEPATH);
        assertTrue(result);
    }

    @Test
    public void testOpenDocumentException() {
        try (MockedStatic<APIClient> mockedApiClient = Mockito.mockStatic(APIClient.class)) {
            mockedApiClient.when(() -> APIClient.sendText(anyString(), anyString()))
                    .thenThrow(new RuntimeException());

            boolean result = testPDFManager.openDocument(FILEPATH);
            assertFalse(result);
        } 
    }

    @Test
    public void testOpenDocumentExceedMaxLimit() {

        for (int i = 0; i < testPDFManager.MAX_DOCUMENT; i++) {
            testPDFManager.openDocument(FILEPATH);
        }

        boolean result = testPDFManager.openDocument(FILEPATH);
        assertFalse(result);
        
    }

    @Test
    public void testCloseDocument() {
        testPDFManager.openDocument(FILEPATH);

        boolean result = testPDFManager.closeDocument(0);
        assertTrue(result);
    }

    @Test
    public void testCloseDocumentException() {
        testPDFManager.openDocument(FILEPATH);

        try (MockedStatic<APIClient> mockedApiClient = Mockito.mockStatic(APIClient.class)) {
            mockedApiClient.when(() -> APIClient.sendDelete(anyString()))
                    .thenThrow(new RuntimeException());

        } catch (RuntimeException e) {
            boolean result = testPDFManager.closeDocument(0);
            assertFalse(result);
        }
    }

    @Test
    public void testCloseDocumentEmpty() {
        while(testPDFManager.closeDocument(0)) {}
        boolean result = testPDFManager.closeDocument(0);
        assertFalse(result);
    }

    @Test
    public void testSearchLexical() throws IOException {
        testPDFManager.openDocument(FILEPATH);
        String[] targets = {"target1", "target2"};
        String[] connectors = {"AND"};

        boolean result = testPDFManager.searchLexical(0, targets, connectors, "WORD");
        assertTrue(result);
    }

    @Test
    public void testSearchKeyword() throws IOException {
        testPDFManager.openDocument(FILEPATH);

        boolean result = testPDFManager.searchKeyword(0, "test", "NOUN", true);
        assertTrue(result);
    }

    @Test
    public void testSearchSemantic() throws IOException {
        testPDFManager.openDocument(FILEPATH);

        boolean result = testPDFManager.searchSemantic(0, "test", 75);
        assertTrue(result);
    }

    @Test
    public void testInvalidIndex() throws IOException {
        int index = 30;
        String[] targets = {"target1", "target2"};
        String[] connectors = {"AND"};
        boolean result = testPDFManager.searchLexical(index, targets, connectors, "WORD");
        assertFalse(result);

        result = testPDFManager.searchKeyword(index, "test", "NOUN", true);
        assertFalse(result);

        result = testPDFManager.searchSemantic(index, "test", 75);
        assertFalse(result);
    }

    @Test
    public void testEmptyTarget() throws IOException {
        testPDFManager.openDocument(FILEPATH);
        int index = 0;
        String[] targets = {};
        String[] connectors = {"AND"};
        String target = "";
        boolean result = testPDFManager.searchLexical(index, targets, connectors, "WORD");
        assertFalse(result);

        result = testPDFManager.searchKeyword(index, target, null, true);
        assertFalse(result);

        result = testPDFManager.searchKeyword(index, target, "NOUN", true);
        assertTrue(result);

        result = testPDFManager.searchSemantic(index, target, 75);
        assertFalse(result);
    }

    @Test
    public void testSearchExceptions() throws IOException {
        testPDFManager.openDocument(FILEPATH);
        String[] targets = {"target1"};
        String[] connectors = {"AND"};


        try (MockedStatic<APIClient> mockedApiClient = Mockito.mockStatic(APIClient.class)) {
            mockedApiClient.when(() -> APIClient.sendSearchLexical(anyString(), any(), any(), anyString()))
                .thenThrow(new RuntimeException());
            mockedApiClient.when(() -> APIClient.sendSearchKeyword(anyString(), anyString(), anyString(), anyBoolean()))
                .thenThrow(new RuntimeException());
            mockedApiClient.when(() -> APIClient.sendSearchSemantic(anyString(), anyString(), anyInt()))
                .thenThrow(new RuntimeException());

            boolean result = testPDFManager.searchLexical(0, targets, connectors, "WORD");
            assertFalse(result);
            result = testPDFManager.searchKeyword(0, "test", "NOUN", true);
            assertFalse(result);
            result = testPDFManager.searchSemantic(0, "test", 75);
            assertFalse(result);
        } 
    }

    @Test
    public void testIdToIndex() {
        testPDFManager.openDocument(FILEPATH);
        int result = PDFManager.idToIndex("0");
        assertNotNull(result);
    }

    @Test
    public void testShutdown() {
        try (MockedStatic<ControlUtil> mockedControlUtil = Mockito.mockStatic(ControlUtil.class)) {
            mockedControlUtil.when(() -> ControlUtil.stopResultQueueThread())
                .thenAnswer(invocation -> null);
            mockedControlUtil.when(() -> ControlUtil.stopSearchEngineThread())
                .thenAnswer(invocation -> null);
            testPDFManager.shutdown();
        }
    }

}
