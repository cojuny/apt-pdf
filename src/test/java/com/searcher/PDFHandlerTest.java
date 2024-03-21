package com.searcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PDFHandlerTest {

    private PDDocument testDocument;

    @Before
    public void setupDocument() throws IOException {
        testDocument = PDFHandler.loadDocument("src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf");
    }

    @After
    public void closeDocument() throws IOException {
        PDFHandler.closeDocument(testDocument);
    }

    @Test
    public void testLoadDocumentValid() throws IOException {
        assertNotNull(testDocument);
    }
    
    @Test(expected = IOException.class)
    public void testLoadDocumentInvalid() throws IOException {
        PDDocument invalid_document = PDFHandler.loadDocument("invalid.pdf");
        assertNull(invalid_document);
    }

    
    @Test
    public void testExtractText() throws IOException {
        String text = PDFHandler.extractText(testDocument);
        assertEquals(54303, text.length());
        assertEquals("Department of Computing", text.substring(12, 35));
    }
    
    @Test
    public void testExtractTitle() {
        String title = PDFHandler.extractTitle("src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf");
        assertEquals("CP Handbook_2023-24_230915", title);
    }

    @Test
    public void testIsFileValid() throws IOException {
        assertEquals(true, PDFHandler.isFileValid("src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf"));
        assertEquals(false, PDFHandler.isFileValid("nofile.pdf"));
        assertEquals(false, PDFHandler.isFileValid("src/test/resources/test.txt"));
        assertEquals(false, PDFHandler.isFileValid("src/test/resources/encrypted_pdf.pdf"));

    }
}