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





    
}