package com.searcher;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFDocument {

    private static int instanceCounter = 0;

    private String filepath;
    private String title;
    private String id;
    private String text;
    protected int page;
    protected int[] pageDiv;

    public PDFDocument(String filepath) {
        try {
            PDDocument document = PDFHandler.loadDocument(filepath);
            this.filepath = filepath;
            title = PDFHandler.extractTitle(filepath);
            page = PDFHandler.extractNumPage(document);
            id = Integer.toString(instanceCounter);
            instanceCounter++;

            StringBuilder builder = new StringBuilder();
            pageDiv = new int[page];
            for (int i = 1; i <= page; i++) {
                builder.append(PDFHandler.extractText(document, i));
                pageDiv[i - 1] = builder.length();
            }
            text = builder.toString();

            PDFHandler.closeDocument(document);
        } catch (IOException e) {
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

}
