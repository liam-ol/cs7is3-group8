import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

// A class to model text embeddings.
public class Embedding {
    String id;
    String embeddingString;
    float[] embeddingFloat;

    // An embedding in the database has two properties:
    // 1. The document id
    // 2. The embedding as a JSON string
    // The class has an embeddingFloat property, 
    // which is the embedding parsed as an array of floats
    public Embedding(String Id, String JsonString) {
        this.id = Id;
        this.embeddingString = JsonString;
        this.embeddingFloat = null;
    }

    // This function converts the embeddingString from JSON string to float array.
    public void convertToFloat(int embeddingSize) {
        float[] embeddingValues;
        try {
            // Parse the JSON object
            JSONObject jsonObject = new JSONObject(this.embeddingString);
            // Extract the embedding array
            JSONArray embeddingArray = jsonObject.getJSONArray("embedding").getJSONArray(0);
            // Convert the JSONArray to a float[] array
            embeddingValues = new float[embeddingArray.length()];
            for (int i = 0; i < embeddingArray.length(); i++) {
                embeddingValues[i] = (float) embeddingArray.getDouble(i);
            }
        }
        catch (Exception e) {
            embeddingValues = new float[embeddingSize];
            Arrays.fill(embeddingValues, 0.0f);
            System.out.println(this.id + "Problematic embedding");
        }
        this.embeddingFloat = embeddingValues;
        this.embeddingString = "";
    }

}
