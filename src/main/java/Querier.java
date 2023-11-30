import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.Similarity;
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
        String[] queryFields = new String[] {"title", "text"}; //{"title", "subtitle", "body", "summary"};
        Map<String, Float> boosts = new HashMap<>();
        boosts.put("text", 0.95f);
        boosts.put("title", 0.05f);
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
        Query query = this.parser.parse(queryString);
        System.out.println("Generated Query: " + query.toString());

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

    public void queryIndex(Topic topic) throws IOException, ParseException {
        
        int docRank = 0;

        StringBuilder relevantBuilder = new StringBuilder();
        StringBuilder irrelevantBuilder = new StringBuilder();
        BreakIterator iter = BreakIterator.getSentenceInstance();
        iter.setText(topic.narrative);
        int lastIndex = iter.first();
        while (lastIndex != BreakIterator.DONE) {
            int firstIndex = lastIndex;
            lastIndex = iter.next();
            if (lastIndex != BreakIterator.DONE) {
                String sentence = topic.narrative.substring(firstIndex, lastIndex);
                if (sentence.contains("not relevant") || sentence.contains("irrelevant")) {
                    irrelevantBuilder.append(sentence);
                }
                else if (sentence.contains("relevant")) {
                    relevantBuilder.append(sentence);
                }

            }
        }

        List<Query> titleQueries = new ArrayList<Query>();
        String[] titleTokens = topic.title.split(",");
        for (String token: titleTokens) {
            titleQueries.add(new TermQuery(new Term("title", token)));
            titleQueries.add(new TermQuery(new Term("text", token)));
        }
        Query descriptionQuery = this.parser.parse(topic.description);

        Query relevantQuery = null, irrelevantQuery = null;
        try {
            String relevant = relevantBuilder.toString();
            relevantQuery = relevant.isEmpty() ? null : this.parser.parse(relevant);
            String irrelevant = irrelevantBuilder.toString();
            irrelevantQuery = irrelevant.isEmpty() ? null : this.parser.parse(irrelevant);
        }
        catch (Exception e) {
            // System.out.println(topic.id);
        }

        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        for (Query titleQuery : titleQueries) {
            queryBuilder.add(new BoostQuery(titleQuery, 0.4f), Occur.SHOULD);
        }
        queryBuilder.add(new BoostQuery(descriptionQuery, 0.6f), Occur.SHOULD);
        if (relevantQuery != null) {
            queryBuilder.add(new BoostQuery(relevantQuery, 0.15f), Occur.SHOULD);
        }
        if (irrelevantQuery != null) {
            queryBuilder.add(new BoostQuery(irrelevantQuery, 0.15f), Occur.FILTER);
        }

        Query query = queryBuilder.build();
        // Get the set of results and write the ID, rank and score of each result in a trec_eval-compatible way.
        ScoreDoc[] hits = this.isearcher.search(query, _MAX_RESULTS).scoreDocs;
        if (hits.length > 0) {
            System.out.println("Results found: " + hits.length);
        }
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            docRank = i + 1;
            this.queryResultsWriter.write(topic.id + " Q0 " + hitDoc.get("id") + " " + docRank + " " + hits[i].score + " STANDARD\n" );
        }
        return;
    }
}