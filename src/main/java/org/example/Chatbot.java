package org.example;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Chatbot {
    private final JSONArray intents;

    public Chatbot(String intentsPath) throws IOException, JSONException {
        String jsonString = Files.readString(Path.of(intentsPath));
        this.intents = new JSONArray(jsonString);
    }

    public String getResponse(String message) {
        String response = "";
        boolean foundMatch = false;
        try {
            for (int i = 0; i < intents.length(); i++) {
                JSONObject intent = intents.getJSONObject(i);
                if (intent.has("patterns")) {
                    JSONArray patterns = intent.getJSONArray("patterns");
                    for (int j = 0; j < patterns.length(); j++) {
                        String pattern = patterns.getString(j);
                        if (message.matches("(?i).*" + pattern + ".*")) {
                            JSONArray responses = intent.getJSONArray("responses");
                            response = responses.getString(new Random().nextInt(responses.length()));
                            foundMatch = true;
                            break;
                        }
                    }
                }
                if (foundMatch) {
                    break;
                }
            }
        } catch (JSONException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
        if (!foundMatch) {
            // If no match is found, do a Google search for the user's input
            try {
                String encodedQuery = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
                response = "I'm sorry, I didn't understand what you said. Here's a Google search result for \"" + message + "\":\nhttps://www.google.com/search?q=" + encodedQuery;
            } catch (Exception e) {
                System.err.println("Error encoding query string: " + e.getMessage());
            }
        }
        return response;
    }
}
