package com.searcher;

import java.util.*;

public class PDFManager {
    
    public List<PDFDocument> documents;
    final public String[] POS_TAG_LIST = {"ADJ", "ADV", "CONJ", "DET", "NOUN", "NUM", "PRON","PREP", "VERB"};
    final public String[] CONNECTOR_LIST = { "AND", "OR", "NOT", "NULL"};
    final public String[] SCOPE_LIST = {"WORD", "SENTENCE", "PARAGRAPH"};
    final public String[] SYNONYMS_LIST = {"ON", "OFF"};
    final public int MIN_THRESHOLD = 50;
    final public int MAX_THRESHOLD = 100;
    final int MAXDOCUMENT = 5;

    public PDFManager() {
        documents = new ArrayList<PDFDocument>();
    }

    public boolean openDocument(String filepath) {
        
        if(documents.size() >= 5) {
            return false;
        }

        try {
            PDFDocument doc = new PDFDocument(filepath);
            documents.add(doc);
            doc.sendTextToServer();
            return true;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } 
        return false;
    }

    public boolean closeDocument(int index) {

        if (!isIndexValid(index)) {return false; }

        documents.remove(index);
        return true;
    }

    public boolean searchLexical(int index, String[] targets, String[] connectors, String scope) {

        if (!isIndexValid(index)) {return false; }

        String response = "";
        boolean res = true;
        try {
            response = APIClient.sendSearchLexical(documents.get(index).getId(), targets, connectors, scope);
        } catch (Exception e) {
            response = e.toString();
            res = false;
        }
        System.out.println(response);
        return res;
    }

    public boolean searchKeyword(int index, String target, String pos, String synonyms) {

        if (!isIndexValid(index)) {return false; }

        String response = "";
        boolean res = true;
        try {
            response = APIClient.sendSearchKeyword(documents.get(index).getId(), target, pos, synonyms);
        } catch (Exception e) {
            response = e.toString();
            res = false;
        }
        System.out.println(response);
        return res;
    }

    public boolean searchSemantic(int index, String target, int threshold) {

        if (!isIndexValid(index)) {return false; }
        if (threshold < MIN_THRESHOLD || threshold > MAX_THRESHOLD) {return false;}

        String response = "";
        boolean res = true;
        try {
            response = APIClient.sendSearchSemantic(documents.get(index).getId(), target, Integer.toString(threshold));
        } catch (Exception e) {
            response = e.toString();
            res = false;
        }
        System.out.println(response);
        return res;
    }

    private boolean isIndexValid(int index) {
        
        if (index < 0 || index >= documents.size()) {
            return false;
        }
        return true;
    }

}
