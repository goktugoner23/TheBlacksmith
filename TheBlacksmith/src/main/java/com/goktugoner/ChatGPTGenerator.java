package com.goktugoner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class ChatGPTGenerator {
    public String generateResponse(String prompt) {
        try {
            OpenAIConnections openai = new OpenAIConnections();
            HttpURLConnection connection = openai.connectChatGPT();

            String payload = "{\"prompt\":\"" + prompt + "\",\"max_tokens\":1000,\"temperature\":0.3}";
            connection.getOutputStream().write(payload.getBytes());

            Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()));
            String response = scanner.useDelimiter("\\A").next();

            int startIndex = response.indexOf("\"text\":") + 8;
            int endIndex = response.indexOf("\",", startIndex);
            System.out.println(response);
            return response.substring(startIndex, endIndex);
        } catch (IOException e) {
            e.printStackTrace();
            return "failure";
        }
    }
}
