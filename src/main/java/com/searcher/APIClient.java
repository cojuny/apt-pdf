package com.searcher;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

public class APIClient {
    
    private static HttpClient centrualizedApiClient = HttpClients.createDefault();
    private static String serverUrl = "http://127.0.0.1:5000";

    public static String sendTextToServer(String id, String text) throws IOException{
        // Create HTTP POST request
        HttpPost httpPost = new HttpPost(serverUrl + "/text");

        // Set the content type as application/json
        httpPost.setHeader("Content-Type", "application/json");

        // Create a JSON object with id and text properties
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("text", text);

        // Create a StringEntity with the JSON object
        StringEntity stringEntity = new StringEntity(json.toString());
        httpPost.setEntity(stringEntity);

        // Execute the request and get the response
        HttpResponse response = centrualizedApiClient.execute(httpPost);

        // Print the response status
        return "Response Status: " + response.getStatusLine().getStatusCode();    } 
    }
 