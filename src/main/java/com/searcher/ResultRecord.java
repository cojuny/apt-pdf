package com.searcher;

public class ResultRecord {
    private String offset;
    private int startIndex;
    private int endIndex;

    public ResultRecord(String offset, int startIndex, int endIndex) {
        this.offset = offset;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    // Getters and Setters
    public String getOffset() {
        return offset;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}