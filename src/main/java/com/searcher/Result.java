package com.searcher;

public class Result {
    private String id;
    private int startIndex;
    private int endIndex;
    private String displayText;

    public Result(String id, int startIndex, int endIndex) {
        this.id = id;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public void setDisplayText(String text) {
        this.displayText = text;
    }

    public String getId() {
        return id;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void processDisplayText(String fullDocument) {
        StringBuilder result = new StringBuilder();
        int resultLen = endIndex - startIndex;

        if (resultLen > 28) {
            result.append("{");
            result.append(fullDocument.substring(startIndex, startIndex + 12));
            result.append("...");
            result.append(fullDocument.substring(endIndex - 12, endIndex));
            result.append("}");
        } else {
            int extraCharLen = (28 - resultLen) / 2;
            int frontPadding = Math.max(0, startIndex - extraCharLen);
            int endPadding = Math.min(fullDocument.length(), endIndex + extraCharLen);
            result.append(fullDocument.substring(frontPadding, startIndex));
            result.append("{");
            result.append(fullDocument.substring(startIndex, endIndex));
            result.append("}");
            result.append(fullDocument.substring(endIndex, endPadding));
        }

        this.displayText = result.toString();
    }

    @Override
    public String toString() {
        return this.displayText;
    }

}