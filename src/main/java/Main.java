import java.nio.file.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.DirectoryReader;

public class Main {

    private static String INDEX_DIRECTORY = "./index-bert";

    public static void main(String[] args) throws Exception {

        /* SEARCH ENGINE PARAMETERS */
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        /* DOCUMENT PARSING AND INDEXING */
        Indexer indexer = new Indexer(directory);
        if (DirectoryReader.indexExists(directory)) {
            System.out.println("Index is already built.");
        } else {
            indexer.readAndIndexDocuments();
        }
        indexer.shutDown();


        /* TOPIC PARSING */
        TopicParser tparser = new TopicParser();

        /* INDEX QUERYING */
        Querier querier = new Querier(directory);
        for (Topic top: tparser.topics) {
            querier.queryIndex(top.id, top.description);
        }
        querier.shutDown();
        directory.close();
    }
}
