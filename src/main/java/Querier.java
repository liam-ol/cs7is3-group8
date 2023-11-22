import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

public class Querier {

    static final Integer _MAX_RESULTS = 1000;
    static final String _QUERY_RESULTS_FILE = "./results.txt";

    private IndexSearcher isearcher;
    private MultiFieldQueryParser parser;
    private BufferedWriter queryResultsWriter;
    
    public Querier(Analyzer engineAnalyzer, Similarity engineSimilarity, Directory indexDirectory) throws IOException {

        // Create an IndexSearcher and set the similarity algorithm.
        DirectoryReader indexDirectoryReader = DirectoryReader.open(indexDirectory);
        this.isearcher = new IndexSearcher(indexDirectoryReader);
        this.isearcher.setSimilarity(engineSimilarity);

        // Create a parser configure it with an analyzer and fields to query.
        String[] queryFields = new String[] {"title", "subtitle", "body", "summary"};
        Map<String, Float> boosts = new HashMap<>();
        boosts.put("body", 5f);
        this.parser = new MultiFieldQueryParser(queryFields, engineAnalyzer, boosts);

        // Open the results file for writing.
        this.queryResultsWriter = new BufferedWriter(new FileWriter(_QUERY_RESULTS_FILE));
        return;
    }

    public void shutDown() throws IOException {
        this.queryResultsWriter.close();
        return;
    }

    public void queryIndex(int queryId, String queryString) throws IOException, ParseException {

        int docRank = 0;

        // Parse the query with the parser.
        Query query = parser.parse(queryString);

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