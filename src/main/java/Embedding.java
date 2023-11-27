import org.json.JSONArray;
import org.json.JSONObject;

public class Embedding {
    String id;
    String embeddingString;
    float[] wordEmbedding;

    public Embedding(String Id, String JsonString) {
        this.id = Id;
        this.embeddingString = JsonString;
        this.wordEmbedding = null;
    }

    public void convertToFloat() {
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
        this.wordEmbedding = embeddingValues;
        this.embeddingString = "";
    }

}
