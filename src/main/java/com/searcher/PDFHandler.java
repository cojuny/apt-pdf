package com.searcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;

public class PDFHandler {

    public static PDDocument loadDocument(String filepath) throws IOException {
        PDDocument document = Loader.loadPDF(new File(filepath));
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

    public static int extractNumPage(PDDocument document) throws IOException {
        return document.getNumberOfPages();
    }

    public static void closeDocument(PDDocument document) throws IOException {
        document.close();
    }

    protected static boolean isFileValid(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            System.err.println("File does not exist.");
            return false;
        } else if (!file.getName().endsWith(".pdf")) {
            System.err.println("File is not in pdf format.");
            return false;
        }
        try {
            Loader.loadPDF(file);
        } catch (IOException e) {
            System.err.println("PDF document is locked or corrupted.");
            return false;
        }
        return true;
    }

}