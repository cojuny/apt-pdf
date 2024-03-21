package com.searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFDocument {

    private static int instanceCounter = 0;

    private String filepath;
    private String title;
    private String id;
    private String text;
    protected int page;
    protected List<Result> results = new ArrayList<>();;

    public PDFDocument(String filepath) {
        try {
            PDDocument document = PDFHandler.loadDocument(filepath);
            this.filepath = filepath;
            title = PDFHandler.extractTitle(filepath);
            page = PDFHandler.extractNumPage(document);
            id = Integer.toString(instanceCounter);
            instanceCounter++;

            text = PDFHandler.extractText(document);

            PDFHandler.closeDocument(document);
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
