package com.searcher;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;

import static org.junit.Assert.*;

public class PDFHandlerTest {

    private PDFHandler pdfHandler;
    private PDDocument testDocument;

    @Before
    public void setUp() {
        pdfHandler = new PDFHandler();
    }

    @After
    public void tearDown() {
        // Close the test document if it was opened
        if (testDocument != null) {
            try {
                testDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testLoadValidDocument() {
        String filePath = "/home/junyoung/files/apt-pdf/src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf";

        try {
            testDocument = pdfHandler.loadDocument(filePath);
            assertNotNull("Document should not be null", testDocument);
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testExtractTextFromDocument() {
        String filePath = "/home/junyoung/files/apt-pdf/src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf";

        try {
            testDocument = pdfHandler.loadDocument(filePath);
            assertNotNull("Document should not be null", testDocument);

            String extractedText = pdfHandler.extractText(testDocument);
            assertNotNull("Extracted text should not be null", extractedText);

            // Print the extracted text (you might want to remove this in a real test)
            System.out.println("Extracted Text:\n" + extractedText);
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoadInvalidDocument() {
        // Providing an invalid file path intentionally
        String filePath = "/invalid/path/to/document.pdf";

        try {
            testDocument = pdfHandler.loadDocument(filePath);
            assertNull("Document should be null for an invalid path", testDocument);
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}