import javax.xml.parsers.*;

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

    /* TODO: add parsing functions for each doc type
    public static Doc parseFBIS() {

    }

    public static Doc parseFR94() {

    }

    public static Doc parseFT() {

    }

    public static Doc parseLATimes() {

    }

     */
}
