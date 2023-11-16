import java.io.*;
import java.nio.file.*;
import javax.xml.parsers.*;
import java.util.ArrayList;

import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

public class Indexer {

    private Analyzer analyzer;
    private Directory directory;
    private Similarity similarity;

    static final String _DOC_ROOT_PATH = "./Assignment Two/";
    static final String _FT_PATH = _DOC_ROOT_PATH + "ft/";
    static final String _FR94_PATH = _DOC_ROOT_PATH + "fr94/";
    static final String _FBIS_PATH = _DOC_ROOT_PATH + "fbis/";
    static final String _LATIMES_PATH = _DOC_ROOT_PATH + "latimes/";

    //public Indexer(Analyzer global_analyzer, Similarity global_similarity, Directory global_directory)
    public Indexer() {
        this.analyzer = null; //global_analyzer;
        this.directory = null; //global_directory;
        this.similarity = null; //global_similarity;
    }

    // This function reads all the files in a directory and appends them to a list.
    // If the directory has subdirectories, the function is called recursively.
    private void createFileList(File[] files, ArrayList<File> fileList) {
        for (File file : files) {
            if (file.isDirectory()) {
                createFileList(file.listFiles(), fileList); // Calls same method again.
            } else {
                if (!file.getName().startsWith("read")) {
                    fileList.add(file);
                }
            }
        }
        return;
    }

    // This function creates a list of all the documents to be indexed.
    // TODO: Parse the documents in the list.
    // TODO: Index the parsed documents.
    public void readDocuments() throws Exception {
        File currDir = null;
        String[] docRootPaths = {_FBIS_PATH, _FR94_PATH, _FT_PATH, _LATIMES_PATH};
        ArrayList<Doc> docList = new ArrayList<Doc>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        currDir = new File(_FT_PATH);
        ArrayList<File> filesToParse = new ArrayList<File>();
        createFileList(currDir.listFiles(), filesToParse);
        System.out.println("filesToParse size for FT: " + filesToParse.size());

        // for (File currFile : filesToParse) {
        //     String[] docsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get(currFile.getAbsolutePath()))));
        //     for (String currDoc : docsRaw) {
        //         docList.add(DocParser.parseFT(currDoc, builder));
        //     }
        // }
        // System.out.println("FT documents parsed: " + docList.size());
        // System.out.println();

        currDir = new File(_FBIS_PATH);
        filesToParse = new ArrayList<File>();
        createFileList(currDir.listFiles(), filesToParse);
        System.out.println("filesToParse size for FBIS: " + filesToParse.size());

        for (File currFile : filesToParse) {
            String[] docsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get(currFile.getAbsolutePath()))));
            for (String currDoc : docsRaw) {
                docList.add(DocParser.parseFBIS(currDoc, builder));
            }
        }
        System.out.println("FBIS documents parsed: " + docList.size());
        System.out.println();
    }

}