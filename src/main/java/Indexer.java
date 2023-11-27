import java.io.*;
import java.nio.file.*;

import javax.imageio.spi.ServiceRegistry;
import javax.xml.parsers.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.VectorSimilarityFunction;
// import org.apache.lucene.index.VectorValues;
// import org.apache.lucene.document.Field;
// import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.KnnVectorField;
// import org.apache.lucene.document.TextField;
// import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;

public class Indexer {

    private IndexWriter iwriter;
    // private ApiClient embeddingFetcher;
    private DbInterface db;

    static final String _DOC_ROOT_PATH = "./Assignment Two/";
    static final String _FT_PATH = _DOC_ROOT_PATH + "ft/";
    static final String _FR94_PATH = _DOC_ROOT_PATH + "fr94/";
    static final String _FBIS_PATH = _DOC_ROOT_PATH + "fbis/";
    static final String _LATIMES_PATH = _DOC_ROOT_PATH + "latimes/";

    public Indexer(Analyzer engineAnalyzer, Similarity engineSimilarity, Directory indexDirectory) throws Exception {
        // IndexWriterConfig config = new IndexWriterConfig(engineAnalyzer);
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        // config.setSimilarity(engineSimilarity);
        this.iwriter = new IndexWriter(indexDirectory, config);
        // this.embeddingFetcher = new ApiClient();
        this.db = new DbInterface();
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
    private void indexDocument(Doc parsedDocument) throws Exception {
        Document luceneDocument = new Document();
        Embedding emb = db.getEmbedding(parsedDocument.id);
        // float[] docVector = this.embeddingFetcher.fetchEmbedding(parsedDocument.body);
        luceneDocument.add(new KnnVectorField("body", emb.wordEmbedding, VectorSimilarityFunction.DOT_PRODUCT));
        // luceneDocument.add(new StringField("id", parsedDocument.id, Field.Store.YES));
        // luceneDocument.add(new TextField("title", parsedDocument.title, Field.Store.YES));
        // luceneDocument.add(new TextField("subtitle", parsedDocument.subtitle, Field.Store.YES));
        // luceneDocument.add(new TextField("summary", parsedDocument.summary, Field.Store.YES));
        // luceneDocument.add(new TextField("body", parsedDocument.body, Field.Store.YES));
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

        DbInterface db = new DbInterface();
        db.startSession();

        File currDir = null;
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        currDir = new File(_FT_PATH);
        ArrayList<File> filesToParse = new ArrayList<>();
        createFileList(currDir.listFiles(), filesToParse);
        System.out.println("filesToParse size for FT: " + filesToParse.size());
        
        // Doc tmpDoc;
        for (File currFile : filesToParse) {
            String[] docsRaw = DocParser.getDocList(new String(Files.readAllBytes(Paths.get(currFile.getAbsolutePath()))));
            for (String currDoc : docsRaw) {
                indexDocument(DocParser.parseFT(currDoc, builder));
                // tmpDoc = DocParser.parseFT(currDoc, builder);
                // db.addDocument(tmpDoc);
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
                // tmpDoc = DocParser.parseFBIS(currDoc);
                // db.addDocument(tmpDoc);
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
                    // tmpDoc = DocParser.parseFR94(currDoc, builder);
                    // db.addDocument(tmpDoc);

                } catch (Exception e) {
                    System.out.println(currDoc);
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
                    // tmpDoc = DocParser.parseLATimes(currDoc, builder);
                    // db.addDocument(tmpDoc);
                } catch (Exception e) {
                    System.out.println(currDoc);
                }
            }
        }

        System.out.println("Finished storing all documents in database");
        db.shutDown();
    }

}