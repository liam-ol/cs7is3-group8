import java.io.*;
import java.nio.file.*;
import javax.xml.parsers.*;
import java.util.ArrayList;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

public class Indexer {

    private IndexWriter iwriter;

    static final String _DOC_ROOT_PATH = "./Assignment Two/";
    static final String _FT_PATH = _DOC_ROOT_PATH + "ft/";
    static final String _FR94_PATH = _DOC_ROOT_PATH + "fr94/";
    static final String _FBIS_PATH = _DOC_ROOT_PATH + "fbis/";
    static final String _LATIMES_PATH = _DOC_ROOT_PATH + "latimes/";

    public Indexer(Analyzer engineAnalyzer, Similarity engineSimilarity, Directory indexDirectory) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(engineAnalyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setSimilarity(engineSimilarity);
        this.iwriter = new IndexWriter(indexDirectory, config);
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

    // This function receives a parsed document and indexes it.
    private void indexDocument(Doc parsedDocument) throws IOException {
        Document luceneDocument = new Document();
        luceneDocument.add(new StringField("id", parsedDocument.id, Field.Store.YES));
        luceneDocument.add(new TextField("title", parsedDocument.title, Field.Store.YES));
        luceneDocument.add(new TextField("subtitle", parsedDocument.subtitle, Field.Store.YES));
        luceneDocument.add(new TextField("summary", parsedDocument.summary, Field.Store.NO));
        luceneDocument.add(new TextField("body", parsedDocument.body, Field.Store.NO));
        iwriter.addDocument(luceneDocument);
        return;
    }

    // This function commits all writes in the index and closes it gracefully.
    public void shutDown() throws IOException {
        this.iwriter.close();
        return;
    }

    // This function read all documents to be indexed
    // and passes them to the indexDocument function.
    public void readAndIndexDocuments() throws Exception {
        File currDir = null;
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        currDir = new File(_FT_PATH);
        ArrayList<File> filesToParse = new ArrayList<>();
        createFileList(currDir.listFiles(), filesToParse);
        System.out.println("filesToParse size for FT: " + filesToParse.size());

        for (File currFile : filesToParse) {
            String[] docsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get(currFile.getAbsolutePath()))));
            for (String currDoc : docsRaw) {
                indexDocument(DocParser.parseFT(currDoc, builder));
            }
        }

        currDir = new File(_FBIS_PATH);
        filesToParse = new ArrayList<>();
        createFileList(currDir.listFiles(), filesToParse);
        System.out.println("filesToParse size for FBIS: " + filesToParse.size());

        for (File currFile : filesToParse) {
            String[] docsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get(currFile.getAbsolutePath()))));
            for (String currDoc : docsRaw) {
                indexDocument(DocParser.parseFBIS(currDoc));
            }
        }

        currDir = new File(_FR94_PATH);
        filesToParse = new ArrayList<>();
        createFileList(currDir.listFiles(), filesToParse);
        System.out.println("filesToParse size for FR94: " + filesToParse.size());

        for (File currFile : filesToParse) {
            String[] docsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get(currFile.getAbsolutePath()))));
            for (String currDoc : docsRaw) {
                try {
                    indexDocument(DocParser.parseFR94(currDoc, builder));
                } catch (Exception e) {
                    //System.out.println(currDoc);
                }
            }
        }

        currDir = new File(_LATIMES_PATH);
        filesToParse = new ArrayList<>();
        createFileList(currDir.listFiles(), filesToParse);
        System.out.println("filesToParse size for LA Times: " + filesToParse.size());

        for (File currFile : filesToParse) {
            String[] docsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get(currFile.getAbsolutePath()))));
            for (String currDoc : docsRaw) {
                try {
                    indexDocument(DocParser.parseLATimes(currDoc, builder));
                } catch (Exception e) {
                    //System.out.println(currDoc);
                }
            }
        }
    }

}