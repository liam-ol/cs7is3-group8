import org.apache.lucene.*;

import javax.xml.parsers.*;

import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws Exception {
        /* DOCUMENT PARSING */
        Indexer indexer = new Indexer();
        indexer.readDocuments();

        /* TOPIC PARSING */
        TopicParser tparser = new TopicParser();
        System.out.println("The following topics have been parsed:");
        for (Topic top: tparser.topics) {
            System.out.print(top.id + ", ");
        }
        System.out.println();
    }
}
