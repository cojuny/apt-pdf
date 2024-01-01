package com.searcher;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PDFDocumentTest {

    PDFDocument testPDF;

    @Before
    public void startEngine() throws IOException, InterruptedException {
        testPDF = new PDFDocument("src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf");
        ControlUtil.startSearchEngineThread();
    }

    @After
    public void stopEngine() throws IOException {
        ControlUtil.stopSearchEngineThread();
    }


    @Test
    public void testAttributes() {
        assertEquals("src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf", testPDF.getFilePath());
        assertEquals("0", testPDF.getId()); // Assuming it starts from 0
        assertEquals("CP Handbook_2023-24_230915", testPDF.getTitle());
        assertEquals(54303, testPDF.getText().length());
        assertEquals("Department of Computing", testPDF.getText().substring(12, 35));
    }

    @Test
    public void testSendTextToServer() throws IOException {
        String response = "";
        try {
            response = testPDF.sendTextToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        assertThat(response, containsString("200"));
        ControlUtil.stopSearchEngineThread();
    }
}
