package com.goktugoner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class DallEGenerator {

    public String returnImageUrl(String prompt){
        try{
            URL url = new URL("https://api.openai.com/v1/images/generations");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer <API_KEY>");
            con.setDoOutput(true);

            String jsonInputString = "{\"model\":\"image-alpha-001\",\"prompt\":\"" + prompt + "\"}";

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = con.getInputStream();
                String responseString = new BufferedReader(new InputStreamReader(responseStream))
                        .lines().collect(Collectors.joining("\n"));
                JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
                return responseJson.get("data").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
            }
        }catch (Exception e) {
                e.printStackTrace();
                return "failure";
            }
        return "failure";
    }
}
