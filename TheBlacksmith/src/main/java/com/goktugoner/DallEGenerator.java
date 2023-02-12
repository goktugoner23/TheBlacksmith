package com.goktugoner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;

public class DallEGenerator {

    public String returnImageUrl(String prompt) {
        try {
            //try to connect
            OpenAIConnections openai = new OpenAIConnections();
            HttpURLConnection con = openai.connectDallE();

            String jsonInputString = "{\"model\":\"image-alpha-001\",\"prompt\":\"" + prompt + "\"}";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            //if connection's ok parse the response and get the image link
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = con.getInputStream();
                String responseString = new BufferedReader(new InputStreamReader(responseStream))
                        .lines().collect(Collectors.joining("\n"));
                JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
                return responseJson.get("data").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString(); //return image url
            }else{
                return "failure";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "failure";
        }
    }
}
