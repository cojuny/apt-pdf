package com.searcher;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class PDFDocumentTest {

    @Before
    public void startEngine() {
        ControlUtil.startSearchEngineThread();
    }

    @After
    public void stopEngine() {
        ControlUtil.stopSearchEngineThread();
    }

    @Test
    public void testInitialization() {
        String filepath = "src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf";
        PDFDocument pdfDocument = new PDFDocument(filepath);

        assertNotNull(pdfDocument.getDocument());
        assertEquals(filepath, pdfDocument.getFilePath());
        assertEquals("0", pdfDocument.getId()); // Assuming it starts from 0
        assertEquals("CP Handbook_2023-24_230915", pdfDocument.getTitle());
        assertNotNull(pdfDocument.getText());
    }

    @Test
    public void testSendTextToServer() throws IOException {
        String filepath = "src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf";
        PDFDocument pdfDocument = new PDFDocument(filepath);

        assertThat(pdfDocument.sendTextToServer(), containsString("200"));
        
    }
}
