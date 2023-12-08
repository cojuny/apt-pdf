package com.searcher;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;

public class PDFHandler {

    private PDFTextStripper handler = new PDFTextStripper();

    public PDFHandler() {
        handler.setSortByPosition(true);
    }

    public PDDocument loadDocument(String filepath) throws IOException {
        PDDocument document = null;
        File file = new File(filepath);
        if (!isFileValid(file)) {
            return document;
        }

        
        try {
            document = Loader.loadPDF(file);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return document;
    }

    public String extractText(PDDocument document) throws IOException {
        return handler.getText(document);
    }

    private boolean isFileValid(File file) {
        if (!file.exists()) {
            System.err.println("File does not exist.");
            return false;
        } else if  (!file.canRead()) {
            System.err.println("File is not readable: " + file.getAbsolutePath());
            return false; 
        } else if (!file.getName().endsWith(".pdf")) {
            System.err.println("File is not in pdf format.");
            return false;
        }
        return true;
        
    }
    
}
