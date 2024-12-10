package com.example;
//Imports
//JSON processor
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
//HTTP client
import okhttp3.*;
//Import standard java libraries
import java.io.IOException;
import java.util.Scanner;
//Unused array imports
import java.util.ArrayList;
import java.util.List;


//Main class
public class Main {
    //initialize variables
    ///OpenAI URL
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    ///API Key
    private static final String API_KEY = ""; // Replace with your actual API key
    //Initialize objects
    ///OkHTTP object  
    private static final OkHttpClient client = new OkHttpClient();
    ///Jackson object
    private static final ObjectMapper mapper = new ObjectMapper();
    
    //Main method
    public static void main(String[] args) {
        //Initialize Scanner
        Scanner scanner = new Scanner(System.in);
        //Text UI
        System.out.println("Console Chat (type 'exit' to quit)");
        //Demarcation
        System.out.println("----------------------------------------");

        //Iterate through user input
        while (true) {
            System.out.print("\nYou: ");
            String userInput = scanner.nextLine().trim();
            
            //exit if user types exit (case-insensitive)
            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }
            
            //Try-catch
            try {
                //Send user input
                String response = sendMessage(userInput);
                //Retrieve GPT input
                System.out.println("\nChatGPT: " + response);
                //Error handling
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        //Close user input
        scanner.close();
    }//end of main method

    //Private method
    private static String sendMessage(String message) throws IOException {
        //Generate the JSON body
        String jsonBody = String.format("""
            {
                "model": "gpt-4o",
                "messages": [
                    {
                        "role": "user",
                        "content": "%s"
                    },
                    {
                        "role": "system",
                        "content": "descriptive answers"
                    }
                ]
            }""", message);

        //Build the HTTP request to send to chatgpt
        ///Initialize the builder object
        Request request = new Request.Builder()
            //Specify chatGPT URL
            .url(OPENAI_API_URL)
            //Add JSON header
            .addHeader("Content-Type", "application/json")
            //Add auth header with API key
            .addHeader("Authorization", "Bearer " + API_KEY)
            //HTTP POST specification. 
            .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
            //Build the request
            .build();

        //Send the request and get the response
        //Close the request when the response block completes
        try (Response response = client.newCall(request).execute()) {
            //If response fails then throw exception
            //Returns true if HTTP code 200-299
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            
            //Read response and parse as JSON
            JsonNode jsonResponse = mapper.readTree(response.body().string());
            //Obtain messages, content, and tokens used.
            return jsonResponse.path("choices")
                             .get(0)
                             .path("message")
                             .path("content")
                             .asText();
        } //end of try
    } //end of send message class

} //end of main class
