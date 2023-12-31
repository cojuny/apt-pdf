package com.searcher;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

public class APIClient {
    
    private static HttpClient centrualizedApiClient = HttpClients.createDefault();
    private static String serverUrl = "http://127.0.0.1:5050";

    public static String sendTextToServer(String id, String text) throws IOException{
        
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("text", text);

        try {
            FileWriter myWriter = new FileWriter("filename.txt");
            myWriter.write(json.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }


        return sendToServer("/text", json);
    }

    public static String sendShutdownSignal() throws IOException {
        JSONObject json = new JSONObject();
        return sendToServer("/shutdown", json);
    }

    private static String sendToServer(String path, JSONObject json) throws IOException {
        HttpPost httpPost = new HttpPost(serverUrl + path);
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity stringEntity = new StringEntity(json.toString());
        httpPost.setEntity(stringEntity);
        HttpResponse response = centrualizedApiClient.execute(httpPost);
        return "Response Status: " + response.getStatusLine().getStatusCode();
    }
}

    
 