package com.searcher;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFDocument {

    private static int instanceCounter = 0;

    private String text;
    private String filepath;
    private String title;
    private String id;
    
    public PDFDocument(String filepath) {
        try {
            PDDocument document = PDFHandler.loadDocument(filepath);
            this.filepath = filepath;
            title = PDFHandler.extractTitle(filepath);
            text = PDFHandler.extractText(document);
            id = Integer.toString(instanceCounter);
            PDFHandler.closeDocument(document);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } 
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
        return response;
    }

}
