import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiClient {

    private String targetUrl;

    public ApiClient() {
        this.targetUrl = "http://localhost:5000/embedding";
    }

    public float[] fetchEmbedding (String text) throws Exception {

        // JSON payload
        // String jsonPayload = "{\"text\": \"" + text + "\"}";
        String jsonPayload = new JSONObject().put("text", text).toString();

        // Create a URL object
        URL apiUrl = new URL(this.targetUrl);

        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

        // Set the request method to POST
        connection.setRequestMethod("POST");

        // Set the content type to JSON
        connection.setRequestProperty("Content-Type", "application/json");

        // Enable input/output streams
        connection.setDoOutput(true);

        // Write the JSON payload to the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Get the response code
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("Response Code: " + responseCode);
        }

        // Read the response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // System.out.println("Response: " + response.toString());
        }

        // Close the connection
        connection.disconnect();

        // Parse the JSON response
        JSONObject jsonObject = new JSONObject(response.toString());

        // Extract the embedding array
        JSONArray embeddingArray = jsonObject.getJSONArray("embedding");

        // Extract the first element of the embedding array
        JSONArray firstElementArray = embeddingArray.getJSONArray(0);

        // Convert the JSONArray to a float[] array
        float[] embeddingValues = new float[firstElementArray.length()];
        for (int i = 0; i < firstElementArray.length(); i++) {
            embeddingValues[i] = (float) firstElementArray.getDouble(i);
        }

        return embeddingValues;

    }
}
