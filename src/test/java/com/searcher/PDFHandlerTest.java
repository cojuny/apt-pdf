package com.searcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

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
        File f1 = tempFolder.newFile("valid_pdf.pdf");
        File f2 = new File(tempFolder.getRoot(), "non_existent_file.pdf");
        File f3 = tempFolder.newFile("non_readable_file.pdf");
        f3.setReadable(false);
        File f4 = tempFolder.newFile("non_pdf_file.txt");

        assertTrue(PDFHandler.isFileValid(f1));
        assertFalse(PDFHandler.isFileValid(f2));
        assertFalse(PDFHandler.isFileValid(f3));
        assertFalse(PDFHandler.isFileValid(f4));
        
    }
}