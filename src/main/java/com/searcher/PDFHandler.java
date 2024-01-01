package com.searcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;

public class PDFHandler {

    public static PDDocument loadDocument(String filepath) throws IOException {
        PDDocument document = null;
        File file = new File(filepath);
        if (!isFileValid(file)) {
            throw new IOException("Invalid file: " + filepath);
        }

        try {
            document = Loader.loadPDF(file);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return document;
    }

    public static String extractText(PDDocument document) throws IOException {
        PDFTextStripper handler = new PDFTextStripper();
        handler.setSortByPosition(true);
        return handler.getText(document);
    }

    public static String extractTitle(String filepath) {
        return Paths.get(filepath).getFileName().toString().replaceFirst("[.][^.]+$", "");
    }

    public static void closeDocument(PDDocument document) throws IOException {
        document.close();
    }

    protected static boolean isFileValid(File file) {
        if (!file.exists()) {
            System.err.println("File does not exist.");
            return false;
        } else if (!file.canRead()) {
            System.err.println("File is not readable: " + file.getAbsolutePath());
            return false;
        } else if (!file.getName().endsWith(".pdf")) {
            System.err.println("File is not in pdf format.");
            return false;
        }
        return true;
    }


    
}