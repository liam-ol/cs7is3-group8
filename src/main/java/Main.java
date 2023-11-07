//package search.engine;

import org.apache.lucene.*;
import java.io.IOException;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws IOException {
        TopicParser tparser = new TopicParser();
        System.out.println("The following topics have been parsed:");
        for (Topic top: tparser.topics) {
            System.out.print(top.id + ", ");
        }
        System.out.println();
    }
}
