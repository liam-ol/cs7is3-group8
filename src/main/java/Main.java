import org.apache.lucene.*;

import javax.xml.parsers.*;

import java.nio.file.*;

public class Main {
    public static void main(String[] args) throws Exception {
        /* DOCUMENT PARSING */
        // This DocumentBuilder instance is passed to every parse function call, saving resources.
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        // Test parsing of a document from the FR94 set.
        String[] testDocsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get("./Assignment Two/fr94/01/fr940104.0"))));
        Doc testDoc = DocParser.parseFR94(testDocsRaw[1], builder);
        testDoc.print();

        /* TOPIC PARSING */
        TopicParser tparser = new TopicParser();
        System.out.println("The following topics have been parsed:");
        for (Topic top: tparser.topics) {
            System.out.print(top.id + ", ");
        }
        System.out.println();
    }
}
