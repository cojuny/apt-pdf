package com.searcher;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class APIClientTest {
    
    private WireMockServer mockServer;

    @Before
    public void setUp() {
        mockServer = new WireMockServer(5050); // No-args constructor will start on port 8080, we need 5050
        mockServer.start();
        WireMock.configureFor("127.0.0.1", mockServer.port());
    }    

    @After
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void testSendText() throws IOException {
        stubFor(post(urlEqualTo("/text"))
        .willReturn(aResponse()
                .withStatus(200)
                .withBody("Response from Text")));

    String response = APIClient.sendText("1", "Hello World");
    assertEquals("Response Status: 200", response);
    }


    @Test
    public void testSendSearchLexical() throws IOException {
        stubFor(post(urlEqualTo("/lexical"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Response from Lexical")));

        String response = APIClient.sendSearchLexical("1", new String[]{"target1, target2"}, new String[]{"NULL", "AND"}, "sentence");
        assertEquals("Response Status: 200", response);
    }

    @Test
    public void testSendSearchKeyword() throws IOException {
        stubFor(post(urlEqualTo("/keyword"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Response from Keyword")));

        String response = APIClient.sendSearchKeyword("1", "target", "NOUN", "True");
        assertEquals("Response Status: 200", response);
    }

    @Test
    public void testSendSearchSemantic() throws IOException {
        stubFor(post(urlEqualTo("/semantic"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Response from Semantic")));

        String response = APIClient.sendSearchSemantic("1", "looking for target.", "40");
        assertEquals("Response Status: 200", response);
    }

    @Test
    public void testSendDeleteSignal() throws IOException {
        stubFor(post(urlEqualTo("/delete"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Response from Delete Singal")));

        String response = APIClient.sendDeleteSignal("1");
        assertEquals("Response Status: 200", response);
    }

}