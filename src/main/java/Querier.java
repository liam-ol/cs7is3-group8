import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.KnnVectorQuery;

public class Querier {

    static final Integer _MAX_RESULTS = 1000;
    static final String _QUERY_RESULTS_FILE = "./results.txt";

    private IndexSearcher isearcher;
    private String transformerModel;
    private BufferedWriter queryResultsWriter;
    
    public Querier(Directory indexDirectory, String transformerModel) throws IOException {

        // Create an IndexSearcher.
        DirectoryReader indexDirectoryReader = DirectoryReader.open(indexDirectory);
        this.isearcher = new IndexSearcher(indexDirectoryReader);

        // Open the results file for writing.
        this.queryResultsWriter = new BufferedWriter(new FileWriter(_QUERY_RESULTS_FILE));

        this.transformerModel = transformerModel;
        return;
    }
    
    public void queryIndex(int queryId, String queryString) throws Exception {
        
        int docRank = 0;

        System.out.print("Processing topic " + queryId + "... ");
        
        // Fetch the text embedding of the query and generate a vector query.
        float[] queryVector = fetchEmbedding(queryString);
        KnnVectorQuery query = new KnnVectorQuery("body", queryVector, _MAX_RESULTS);
        
        // Get the set of results and write the ID, rank and score of each result in a trec_eval-compatible way.
        ScoreDoc[] hits = this.isearcher.search(query, _MAX_RESULTS).scoreDocs;
        System.out.println(hits.length + " results found");
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            docRank = i + 1;
            this.queryResultsWriter.write(queryId + " Q0 " + hitDoc.get("id") + " " + docRank + " " + hits[i].score + " STANDARD\n" );
        }
        return;
    }

    private float[] fetchEmbedding(String queryString) {

        try {
            // Run the Python script
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python3", "src/main/python/generate-embedding.py", this.transformerModel, queryString
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
    
            // Read the output using an input stream
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    
            // Read the output line by line
            String line;
            List<Float> floatList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                float value = Float.parseFloat(line.trim());
                floatList.add(value);
            }
    
            // Convert the List<Float> to float[]
            float[] resultArray = new float[floatList.size()];
            for (int i = 0; i < floatList.size(); i++) {
                resultArray[i] = floatList.get(i);
            }
            return resultArray;
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Close the query results writer.
    public void shutDown() throws IOException {
        this.queryResultsWriter.close();
        return;
    }
}