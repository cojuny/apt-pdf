package com.searcher;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PDFHandlerTest {

    private PDDocument testDocument;

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
    public void testLoadDocumentValid() {
        String filePath = "src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf";

        try {
            testDocument = PDFHandler.loadDocument(filePath);
            assertNotNull("Document should not be null", testDocument);
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test(expected = IOException.class)
    public void testLoadDocumentInvalid() throws IOException {
        String filePath = "/invalid/path/to/document.pdf";
        testDocument = PDFHandler.loadDocument(filePath);
        assertNull("Document should be null for an invalid path", testDocument);
    }

    @Test
    public void testExtractAttributesFromDocument() {
        String filePath = "src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf";

        try {
            testDocument = PDFHandler.loadDocument(filePath);
            assertNotNull("Document should not be null", testDocument);

            String extractedText = PDFHandler.extractText(testDocument);
            assertNotNull("Extracted text should not be null", extractedText);

            String extractedTitle = PDFHandler.extractTitle(filePath);
            assertEquals("CP Handbook_2023-24_230915", extractedTitle);

        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    
}