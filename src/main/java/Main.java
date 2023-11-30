import java.nio.file.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.BM25Similarity;

public class Main {

    private static String INDEX_DIRECTORY = "./index";

    public static void main(String[] args) throws Exception {

        /* SEARCH ENGINE PARAMETERS */
        Analyzer analyzer = new EnglishAnalyzer();
        Similarity similarity = new BM25Similarity();
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        /* DOCUMENT PARSING AND INDEXING */
        Indexer indexer = new Indexer(analyzer, similarity, directory);
        if (DirectoryReader.indexExists(directory)) {
            System.out.println("Index is already built.");
        } else {
            indexer.readAndIndexDocuments();
        }

        indexer.shutDown();

        /* TOPIC PARSING */
        TopicParser tparser = new TopicParser();

        /* INDEX QUERYING */
        Querier querier = new Querier(analyzer, similarity, directory);
        for (Topic top: tparser.topics) {
            // querier.queryIndex(top.id, top.description);
            querier.queryIndex(top);
        }
        querier.shutDown();
        directory.close();
    }
}
