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
    public void convertToFloat() {
        // Parse the JSON object
        JSONObject jsonObject = new JSONObject(this.embeddingString);
        // Extract the embedding array
        JSONArray embeddingArray = jsonObject.getJSONArray("embedding");
        // Extract the first element of the embedding array
        JSONArray firstElementArray = embeddingArray.getJSONArray(0);
        // Convert the JSONArray to a float[] array
        float[] embeddingValues = new float[firstElementArray.length()];
        for (int i = 0; i < firstElementArray.length(); i++) {
            embeddingValues[i] = (float) firstElementArray.getDouble(i);
        }
        this.embeddingFloat = embeddingValues;
        this.embeddingString = "";
    }

}
