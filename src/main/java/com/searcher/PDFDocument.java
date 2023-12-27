package com.searcher;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFDocument {

    private static int instanceCounter = 0;

    private PDDocument document;
    private String text;
    private String filepath;
    private String title;
    private String id;
    
    public PDFDocument(String filepath) {
        try {
            this.document = PDFHandler.loadDocument(filepath);
            this.filepath = filepath;
            this.title = PDFHandler.extractTitle(filepath);
            this.text = PDFHandler.extractText(this.document);
            this.id = Integer.toString(instanceCounter);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public PDDocument getDocument() {
        return this.document;
    }
    public String getText() {
        return this.text;
    }
    public String getFilePath() {
        return this.filepath;
    }
    public String getTitle() {
        return this.title;
    }
    public String getId() {
        return this.id;
    }

    public String sendTextToServer() throws IOException {
        String response = APIClient.sendTextToServer(this.id, this.text);
        return(response);
    }

}
