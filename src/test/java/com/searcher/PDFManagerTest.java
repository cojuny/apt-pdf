package com.searcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;

import static org.junit.Assert.*;

public class PDFManagerTest {

    private PDFManager testPDFManager;
    private final String FILEPATH = "src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf";

    @Before
    public void setUp() {
        testPDFManager = new PDFManager();
    }

    @Test
    public void testOpenDocumentSuccess() throws IOException {
        try (MockedStatic<APIClient> mockedApiClient = Mockito.mockStatic(APIClient.class)) {
            mockedApiClient.when(() -> APIClient.sendText(anyString(), anyString()))
                    .thenReturn("Response Status: 200");

            boolean result = testPDFManager.openDocument(FILEPATH);
            assertTrue(result);
            assertEquals(1, testPDFManager.documents.size());
        }
    }

    @Test
    public void testOpenDocumentExceedMaxLimit() {
        for (int i = 0; i < testPDFManager.MAXDOCUMENT; i++) {
            testPDFManager.documents.add(new PDFDocument(FILEPATH));
        }

        boolean result = testPDFManager.openDocument(FILEPATH);
        assertFalse(result);
        assertEquals(testPDFManager.MAXDOCUMENT, testPDFManager.documents.size());
    }

    @Test
    public void testCloseDocument() {
        testPDFManager.documents.add(new PDFDocument(FILEPATH));

        boolean result = testPDFManager.closeDocument(0);
        assertTrue(result);
        assertTrue(testPDFManager.documents.isEmpty());
    }

    @Test
    public void testCloseDocumentInvalidIndex() {
        boolean result = testPDFManager.closeDocument(0);
        assertFalse(result);
    }

    @Test
    public void testSearchLexical() throws IOException {
        testPDFManager.documents.add(new PDFDocument(FILEPATH));
        String[] targets = {"target1", "target2"};
        String[] connectors = {"AND"};

        try (MockedStatic<APIClient> mockedApiClient = Mockito.mockStatic(APIClient.class)) {
            mockedApiClient.when(() -> APIClient.sendSearchLexical(anyString(), eq(targets), eq(connectors), anyString()))
                    .thenReturn("Lexical search successful");

            boolean result = testPDFManager.searchLexical(0, targets, connectors, "WORD");
            assertTrue(result);
        }
    }

    @Test
    public void testSearchKeyword() throws IOException {
        testPDFManager.documents.add(new PDFDocument(FILEPATH));

        try (MockedStatic<APIClient> mockedApiClient = Mockito.mockStatic(APIClient.class)) {
            mockedApiClient.when(() -> APIClient.sendSearchKeyword(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn("Keyword search successful");

            boolean result = testPDFManager.searchKeyword(0, "test", "NOUN", "ON");
            assertTrue(result);
        }
    }

    @Test
    public void testSearchSemantic() throws IOException {
        testPDFManager.documents.add(new PDFDocument(FILEPATH));

        try (MockedStatic<APIClient> mockedApiClient = Mockito.mockStatic(APIClient.class)) {
            mockedApiClient.when(() -> APIClient.sendSearchSemantic(anyString(), anyString(), anyString()))
                    .thenReturn("Semantic search successful");

            boolean result = testPDFManager.searchSemantic(0, "test", 75);
            assertTrue(result);
        }
    }

}
