package com.searcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.pdfbox.pdmodel.PDDocument;

import static org.mockito.Mockito.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PDFDocumentTest {

    @Mock
    private PDDocument mockDocument;

    // Assuming PDFHandler is your class and can be mocked; if not, use alternative approach
    @Mock
    private PDFHandler mockPDFHandler;

    private final String testFilePath = "test.pdf";
    private final String testTitle = "Test Title";
    private final int testPages = 5;
    private final String testText = "This is some text from the PDF.";

    @BeforeEach
    void setUp() throws IOException {
        // Mocking the static methods from PDFHandler using Mockito, replace with PowerMock if necessary
        when(PDFHandler.loadDocument(testFilePath)).thenReturn(mockDocument);
        when(PDFHandler.extractTitle(testFilePath)).thenReturn(testTitle);
        when(PDFHandler.extractNumPage(mockDocument)).thenReturn(testPages);

        // Simulate extracting text from each page
        for (int i = 1; i <= testPages; i++) {
            when(PDFHandler.extractText(mockDocument, i)).thenReturn("Page " + i + " content ");
        }
    }

    @Test
    void testPDFDocumentCreation() throws IOException {
        PDFDocument pdfDocument = new PDFDocument(testFilePath);

        assertNotNull(pdfDocument);
        assertEquals(testTitle, pdfDocument.getTitle());
        assertEquals(testFilePath, pdfDocument.getFilePath());
        assertEquals(Integer.toString(0), pdfDocument.getId()); // Assuming this is the first instance created
        assertNotNull(pdfDocument.getText());
        assertTrue(pdfDocument.getText().contains("Page 1 content"));
        assertEquals(testPages, pdfDocument.page);
        assertArrayEquals(new int[]{14, 29, 44, 59, 74}, pdfDocument.pageDiv); // Assuming "Page X content " is the text for each page

    }

    // Add more tests as needed to cover other scenarios and methods
}
