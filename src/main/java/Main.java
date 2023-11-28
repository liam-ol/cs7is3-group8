import java.nio.file.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.DirectoryReader;

public class Main {

    public static void main(String[] args) throws Exception {

        /* SEARCH ENGINE PARAMETERS */
        String transformerModel = "miniLM";
        String indexDirectory = "./index-" + transformerModel;
        Directory directory = FSDirectory.open(Paths.get(indexDirectory));

        /* DOCUMENT PARSING AND INDEXING */
        Indexer indexer;
        if (DirectoryReader.indexExists(directory)) {
            System.out.println("Index is already built.");
        } else {
            indexer = new Indexer(directory, transformerModel);
            indexer.readAndIndexDocuments();
            indexer.shutDown();
        }


        /* TOPIC PARSING */
        TopicParser tparser = new TopicParser();

        /* INDEX QUERYING */
        Querier querier = new Querier(directory, transformerModel);
        for (Topic top: tparser.topics) {
            querier.queryIndex(top.id, top.description);
        }
        querier.shutDown();
        directory.close();
    }
}
