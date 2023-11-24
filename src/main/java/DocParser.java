import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.io.StringReader;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
                .replaceAll("&.*;", " ");

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

    public static Doc parseFBIS(String docRaw) throws Exception  {
        String docID = "", docTitle = "", docSubtitle = "", docSummary = "", docBody = "";
        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile("<DOCNO>(.*?)</DOCNO>");
        matcher = pattern.matcher(docRaw);
        if (matcher.find()) {
            docID = matcher.group(1).trim();
        }

        pattern = Pattern.compile("<TI>([\\s\\S]*)</TI>");
        matcher = pattern.matcher(docRaw);
        if (matcher.find()) {
            docTitle = matcher.group(1).trim();
        }

        pattern = Pattern.compile("<H4>([\\s\\S]*)</H4>");
        matcher = pattern.matcher(docRaw);
        if (matcher.find()) {
            docSubtitle = matcher.group(1).trim();
        }

        pattern = Pattern.compile("<TEXT>([\\s\\S]*)</TEXT>");
        matcher = pattern.matcher(docRaw);
        if (matcher.find()) {
            docBody = matcher.group(1).trim();
        }

        // Summaries are tricky - many ways they are written.
        // Check for summaries bookended with 'SUMMARY' + 'END SUMMARY'.
        if (docBody.contains("END SUMMARY"))
        {
            docSummary = docBody.split("END SUMMARY")[0]
                    .replaceAll("SUMMARY", "").trim();
        } else if (!docBody.isEmpty()) {
            // For BFN articles, just use the first paragraph.
            docSummary = docBody.split("\\n {2}")[0];
        }
        return new Doc(docID, docTitle, docSubtitle, docSummary, docBody);       

    }

    public static Doc parseFT(String docRaw, DocumentBuilder parser) throws Exception {
        String docID, docTitle, docHeadline, docBody;

        Document doc = readXML(docRaw, parser);
        docID = doc.getElementsByTagName("DOCNO").item(0).getTextContent();

        NodeList body = doc.getElementsByTagName("TEXT");
        docBody = (body.getLength() > 0)
            ? body.item(0).getTextContent().trim()
            : doc.getElementsByTagName("DATELINE").item(0).getTextContent().trim();
        
        NodeList headline = doc.getElementsByTagName("HEADLINE");
        docHeadline = (headline.getLength() > 0)
            ? headline.item(0).getTextContent().trim()
            : "";

        Pattern pattern = Pattern.compile("FT\\s*\\d+\\s*\\S+\\s*\\d+\\s*/\\s*\\(CORRECTED\\)\\s*(?<title>[\\S\\s]+)");
        Matcher matcher = pattern.matcher(docHeadline);
        if (matcher.find()) {
            docTitle = matcher.group("title");
        }
        else {
            docTitle = "";
        }
        return new Doc(docID, docTitle, "", "", docBody);

    }

    public static Doc parseLATimes(String docRaw, DocumentBuilder parser) throws Exception  {
        String docID, docTitle, docSubtitle, docSummary, docBody;

        // Remove <P> tags.
        String docClean = docRaw.replaceAll("<\\/?P>", "");
        Document doc = readXML(docClean, parser);

        docID = doc.getElementsByTagName("DOCNO").item(0).getTextContent();

        NodeList body = doc.getElementsByTagName("TEXT");
        docBody = (body.getLength() > 0)
                ? body.item(0).getTextContent().trim()
                : doc.getElementsByTagName("GRAPHIC").item(0).getTextContent().trim();

        NodeList title = doc.getElementsByTagName("HEADLINE");
        docTitle = (title.getLength() > 0)
                ? title.item(0).getTextContent().trim()
                : "";

        NodeList subtitle = doc.getElementsByTagName("SUBJECT");
        docSubtitle = (subtitle.getLength() > 0)
                ? subtitle.item(0).getTextContent().trim()
                : "";

        docSummary = docBody.split("\\n\\n\\n")[0];

        return new Doc(docID, docTitle, docSubtitle, docSummary, docBody);
    }
}
