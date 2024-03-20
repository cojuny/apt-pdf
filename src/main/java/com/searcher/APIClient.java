package com.searcher;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

public class APIClient {
    
    private static String serverUrl = "http://127.0.0.1:5050";

    protected static String sendText(String id, String text) throws IOException{
        
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("text", text);

        return sendToServer("/text", json);
    }

    protected static String sendSearchLexical(String id, String[] targets, String[] connectors, String scope) throws IOException {
        
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("targets", targets);
        json.put("connectors", connectors);
        json.put("scope", scope);

        return sendToServer("/lexical", json);
    }

    protected static String sendSearchKeyword(String id, String target, String pos, boolean synonyms) throws IOException {
        
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("target", target);
        json.put("target_pos", pos);
        json.put("synonyms", synonyms);

        return sendToServer("/keyword", json);
    }


    protected static String sendSearchSemantic(String id, String target, int threshold) throws IOException {
        
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("query", target);
        json.put("threshold", threshold);
        
        return sendToServer("/semantic", json);
    }

    protected static String sendDelete(String id) throws IOException {
        
        JSONObject json = new JSONObject();
        json.put("id", id);

        return sendToServer("/delete", json);
    }

    protected static String sendHalt() throws IOException {
        
        JSONObject json = new JSONObject();

        return sendToServer("/halt", json);
    }


    protected static String sendShutdownSignal() throws IOException {

        JSONObject json = new JSONObject();

        return sendToServer("/shutdown", json);
    }

    private static String sendToServer(String path, JSONObject json) {

        try {
            HttpPost request = new HttpPost(serverUrl + path);
            request.setHeader("Content-Type", "application/json");
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
            request.setEntity(stringEntity);
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(request);
            return "Response Status: " + response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return "APIClient.sendToServer has unexpected exception.";
        }
    }
}

    
 