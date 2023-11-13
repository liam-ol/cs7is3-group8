import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

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

    public static Document readXML(String doc, DocumentBuilder parser) throws Exception {
        InputSource docInput = new InputSource(new StringReader(doc));
        return parser.parse(docInput);
    }

    public static Doc parseFR94(String docRaw, DocumentBuilder parser) throws Exception {
        String docID, docTitle, docSummary, docBody;

        // First some bulk replaces: remove all comments and &blank;s
        String docClean = docRaw.replaceAll("<!--.*-->\\n", "")
                .replaceAll("&blank;", " ")
                .replaceAll("&hyph;", "-");

        Document doc = readXML(docClean, parser);

        // All docs have a <DOCNO> and <TEXT>.
        docID = doc.getElementsByTagName("DOCNO").item(0).getTextContent();
        docBody = doc.getElementsByTagName("TEXT").item(0).getTextContent().replaceAll("\n\n","");

        // For other tags, check if they exist first.
        NodeList title = doc.getElementsByTagName("DOCTITLE");
        docTitle = (title.getLength() > 0)
                ? title.item(0).getTextContent()
                : "";

        NodeList summary = doc.getElementsByTagName("SUMMARY");
        docSummary = (summary.getLength() > 0)
                ? summary.item(0).getTextContent()
                    .replace("SUMMARY:","").trim()
                : "";

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
