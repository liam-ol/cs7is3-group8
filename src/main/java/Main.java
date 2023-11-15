import org.apache.lucene.*;

import javax.xml.parsers.*;

import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws Exception {
        /* DOCUMENT PARSING */
        // This DocumentBuilder instance is passed to every parse function call, saving resources.
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        
        Indexer indexer = new Indexer();
        indexer.readDocuments();

        // Test parsing of a document from the FR94 set.
        String[] testDocsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get("./Assignment Two/fr94/01/fr940104.0"))));
        System.out.println("Printing parsed FR94 document");
        Doc testDoc = DocParser.parseFR94(testDocsRaw[1], builder);
        testDoc.print();
        System.out.println();

        // Test parsing of a document from the FT set.
        testDocsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get("./Assignment Two/ft/ft923/ft923_17"))));
        testDoc = DocParser.parseFT(testDocsRaw[0], builder);
        System.out.println("Printing parsed FT document");
        testDoc.print();
        System.out.println();

        // Test parsing of a document from the LA Times set.
        testDocsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get("./Assignment Two/latimes/la010189"))));
        testDoc = DocParser.parseLATimes(testDocsRaw[1], builder);
        System.out.println("Printing parsed FR94 document");
        testDoc.print();
        System.out.println();

        /* TOPIC PARSING */
        TopicParser tparser = new TopicParser();
        System.out.println("The following topics have been parsed:");
        for (Topic top: tparser.topics) {
            System.out.print(top.id + ", ");
        }
        System.out.println();
    }
}
