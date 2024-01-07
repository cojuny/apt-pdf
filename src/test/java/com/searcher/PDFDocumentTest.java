package com.searcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class PDFDocumentTest {

    private PDFDocument testPDF;

    @Before
    public void startEngine() throws IOException {
        testPDF = new PDFDocument("src/main/resources/sample_pdf/CP Handbook_2023-24_230915.pdf");
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
        try (MockedStatic<APIClient> mockedApiClient = mockStatic(APIClient.class)) {
            mockedApiClient.when(() -> APIClient.sendText(anyString(), anyString()))
                    .thenReturn("Response Status: 200");

            String response = testPDF.sendTextToServer();
            assertEquals("Response Status: 200", response);
        }
    }
}
