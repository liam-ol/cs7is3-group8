import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.IOException;

public class DocParser {

    /**
     * Separates a document file into an array of <DOC> objects. Should work in all cases.
     * @param docFile
     * @return
     */
    public static String[] getDocList(String docFile) {
        // Trim file to remove trailing newlines (e.g. fr94)
        return docFile.trim().split("(?<=/DOC>)\\n");
    }

    public static Doc parseFR94(String docRaw, DocumentBuilder parser) throws IOException, SAXException {
        String docTitle, docSummary, docBody;
        // First some bulk replaces: remove all comments and &blank;s
        String docClean = docRaw.replaceAll("<!--.*-->\\n", "")
                .replaceAll("&blank;", " ")
                .replaceAll("&hyph;", "-");

        // TODO: Set up a DocumentBuilder instance in Main - we can pass it into these functions and save time.
        Document doc = parser.parse(docClean);

        // All docs have a <DOCID> and <TEXT>.
        int docID = Integer.parseInt(doc.getElementsByTagName("DOCID").item(0).getTextContent());
        docBody = doc.getElementsByTagName("TEXT").item(0).getTextContent();

        // For other tags, check if they exist first.
        NodeList title = doc.getElementsByTagName("DOCTITLE");
        if (title.getLength() > 0)
            docTitle = title.item(0).getTextContent();
        else docTitle = "";
        NodeList summary = doc.getElementsByTagName("SUMMARY");
        if (summary.getLength() > 0)
            docSummary = summary.item(0).getTextContent();
        else docSummary = "";

        return new Doc(docID, docTitle, "", docSummary, docBody);
    }

    /* TODO: add parsing functions for each other doc type
    public static Doc parseFBIS(String docRaw, DocumentBuilder parser) throws IOException, SAXException  {

    }

    public static Doc parseFT(String docRaw, DocumentBuilder parser) throws IOException, SAXException  {

    }

    public static Doc parseLATimes(String docRaw, DocumentBuilder parser) throws IOException, SAXException  {

    }

     */
}
