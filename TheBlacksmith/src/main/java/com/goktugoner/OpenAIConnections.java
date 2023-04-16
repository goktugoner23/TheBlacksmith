package com.goktugoner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenAIConnections {
    public final String DALLE_API_URL = "https://api.openai.com/v1/images/generations";
    public final String DALLE_API_KEY = "";
    public final String CHATGPT_API_URL = "https://api.openai.com/v1/completions";
    public final String CHATGPT_API_KEY = "";

    public HttpURLConnection connectDallE() throws IOException {
        URL url = new URL(DALLE_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + DALLE_API_KEY); //here goes API Key from OpenAI
        connection.setDoOutput(true);
        return connection;
    }

    public HttpURLConnection connectChatGPT() throws IOException {
        URL url = new URL(CHATGPT_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + CHATGPT_API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }
}
