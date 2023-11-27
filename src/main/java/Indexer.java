import java.io.*;
import java.util.List;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KnnVectorField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;

public class Indexer {

    private IndexWriter iwriter;
    private DbInterface db;

    static final String _DOC_ROOT_PATH = "./Assignment Two/";
    static final String _FT_PATH = _DOC_ROOT_PATH + "ft/";
    static final String _FR94_PATH = _DOC_ROOT_PATH + "fr94/";
    static final String _FBIS_PATH = _DOC_ROOT_PATH + "fbis/";
    static final String _LATIMES_PATH = _DOC_ROOT_PATH + "latimes/";

    public Indexer(Directory indexDirectory) throws Exception {
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        this.iwriter = new IndexWriter(indexDirectory, config);
        this.db = new DbInterface();
    }

    // This function commits all writes in the index and closes it gracefully.
    public void shutDown() throws IOException {
        this.iwriter.close();
        return;
    }

    // This function read all documents to be indexed
    // and passes them to the indexDocument function.
    public void readAndIndexDocuments() throws Exception {

        this.db.startSession();

        Integer count = 0;
        Embedding currEmbedding;
        List<String> docIds = this.db.getDocumentIds();
        for (String docId : docIds) {
            currEmbedding = this.db.getEmbedding(docId);
            if (currEmbedding.wordEmbedding.length != 768) {
                System.out.println("Invalid vector size: " + currEmbedding.id);
            }
            else {
                Document luceneDocument = new Document();
                luceneDocument.add(new StringField("id", currEmbedding.id, Field.Store.YES));
                luceneDocument.add(new KnnVectorField("body", currEmbedding.wordEmbedding, VectorSimilarityFunction.EUCLIDEAN));
                this.iwriter.addDocument(luceneDocument);
                count++;
                System.out.println(count);
            }
        }

        System.out.println("Finished storing all documents in database");
        db.shutDown();
    }

}