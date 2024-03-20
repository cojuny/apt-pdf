package com.searcher;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.IOException;

public class PDFDocumentTest {

    String fakeFilePath = "fakepath.pdf"; 
    String fakeTitle = "Fake Title";
    String fakeText = "Fake Text";

    @Test
    public void testPDFDocument() throws IOException {
        
        try (MockedStatic<PDFHandler> mockedPDFHandler = Mockito.mockStatic(PDFHandler.class)) {
            mockedPDFHandler.when(() -> PDFHandler.loadDocument(anyString()))
                            .thenReturn(Mockito.mock(PDDocument.class)); 
            mockedPDFHandler.when(() -> PDFHandler.extractTitle(anyString()))
                            .thenReturn(fakeTitle);
            mockedPDFHandler.when(() -> PDFHandler.extractNumPage(Mockito.any(PDDocument.class)))
                            .thenReturn(10); 
            mockedPDFHandler.when(() -> PDFHandler.extractText(Mockito.any(PDDocument.class)))
                            .thenReturn(fakeText);

            PDFDocument pdfDocument = new PDFDocument(fakeFilePath);
            String currentID = pdfDocument.getId();

            mockedPDFHandler.verify(() -> PDFHandler.loadDocument(fakeFilePath), times(1));
            mockedPDFHandler.verify(() -> PDFHandler.extractTitle(fakeFilePath), times(1));
            mockedPDFHandler.verify(() -> PDFHandler.extractNumPage(Mockito.any(PDDocument.class)), times(1));

            
            assertEquals(fakeText, pdfDocument.getText());
            assertEquals(fakeFilePath, pdfDocument.getFilePath());
            assertEquals(fakeTitle, pdfDocument.getTitle());
            assertEquals(currentID, pdfDocument.getId());
        }
    }
}