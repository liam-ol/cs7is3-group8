import java.io.FileWriter;
import java.io.IOException;
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
    private BufferedWriter queryResultsWriter;
    private ApiClient embeddingFetcher;
    
    public Querier(Directory indexDirectory) throws IOException {

        // Create an ApiClient which will fetch the query embeddings.
        this.embeddingFetcher = new ApiClient();

        // Create an IndexSearcher.
        DirectoryReader indexDirectoryReader = DirectoryReader.open(indexDirectory);
        this.isearcher = new IndexSearcher(indexDirectoryReader);

        // Open the results file for writing.
        this.queryResultsWriter = new BufferedWriter(new FileWriter(_QUERY_RESULTS_FILE));
        return;
    }

    // Close the query results writer.
    public void shutDown() throws IOException {
        this.queryResultsWriter.close();
        return;
    }

    public void queryIndex(int queryId, String queryString) throws Exception {

        int docRank = 0;

        // Fetch the text embedding of the query and generate a vector query.
        float[] queryVector = this.embeddingFetcher.fetchEmbedding(queryString);
        KnnVectorQuery query = new KnnVectorQuery("body", queryVector, _MAX_RESULTS);

        // Get the set of results and write the ID, rank and score of each result in a trec_eval-compatible way.
        ScoreDoc[] hits = this.isearcher.search(query, _MAX_RESULTS).scoreDocs;
        if (hits.length > 0) {
            System.out.println("Results found: " + hits.length);
        }
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            docRank = i + 1;
            this.queryResultsWriter.write(queryId + " Q0 " + hitDoc.get("id") + " " + docRank + " " + hits[i].score + " STANDARD\n" );
        }
        return;
    }
}