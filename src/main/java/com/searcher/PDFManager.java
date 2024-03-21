package com.searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PDFManager {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    protected static List<PDFDocument> documents = new ArrayList<PDFDocument>();
    protected ResultHandler resultHandler = new ResultHandler();
    final public String[] POS_TAG_LIST = { "ADJ", "ADV", "CONJ", "DET", "NOUN", "NUM", "PRON", "PREP", "VERB" };
    final public String[] CONNECTOR_LIST = { "AND", "OR", "NOT", "NULL" };
    final public String[] SCOPE_LIST = { "WORD", "SENTENCE", "PARAGRAPH" };
    final public String[] SYNONYMS_LIST = { "0", "1" };
    final public int MIN_THRESHOLD = 20;
    final public int MAX_THRESHOLD = 80;
    final int MAX_DOCUMENT = 28;

    public PDFManager() {
        CompletableFuture<Void> startResultQueueFuture = CompletableFuture.runAsync(() -> {
            try {
                ControlUtil.startResultQueueThread();
            } catch (Exception e) {
                System.err.println("Result Queue cannot be started.");
            }
        }, executorService).thenRunAsync(resultHandler::startListening, executorService);

        startResultQueueFuture.thenRunAsync(() -> {
            try {
                ControlUtil.startSearchEngineThread();
            } catch (Exception e) {
                System.err.println("Search Engine cannot be started.");
            }
        }, executorService);
    }

    public boolean openDocument(String filepath) {

        if (documents.size() >= MAX_DOCUMENT) {
            return false;
        }

        try {
            PDFDocument doc = new PDFDocument(filepath);
            APIClient.sendText(doc.getId(), doc.getText());
            documents.add(doc);
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public boolean closeDocument(int index) {

        if (!isIndexValid(index)) {
            return false;
        }
        try {
            APIClient.sendDelete(documents.get(index).getId());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        documents.remove(index);
        return true;
    }

    public static int idToIndex(String id) {
        for (int i = 0; i < documents.size(); i++) {
            if (id.equals(documents.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    public boolean searchLexical(int index, String[] targets, String[] connectors, String scope) {

        if (!isIndexValid(index)) {
            return false;
        }
        if (targets.length == 0) {
            return false;
        }

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

    public boolean searchKeyword(int index, String target, String pos, boolean synonyms) {

        if (!isIndexValid(index)) {
            return false;
        }
        if ((isTargetEmpty(target) && pos == null)) {
            return false;
        }
        if (isTargetEmpty(target)) {
            synonyms = false;
        } // no target no synonyms.

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

        if (!isIndexValid(index) || isTargetEmpty(target)) {
            return false;
        }

        String response = "";
        boolean res = true;
        try {
            response = APIClient.sendSearchSemantic(documents.get(index).getId(), target, threshold);
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

    private boolean isTargetEmpty(String target) {
        return (target == null || target.isEmpty());
    }

    public void shutdown() {
        try {
            resultHandler.stopListening();
            APIClient.sendShutdownSignal();
            Thread.sleep(3000);
            ControlUtil.stopResultQueueThread();
            ControlUtil.stopSearchEngineThread();
        } catch (Exception e) {
            System.err.println("Shutdown Error.");
        } finally {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
