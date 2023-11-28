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
    private String transformerModel;

    static final String _DOC_ROOT_PATH = "./Assignment Two/";
    static final String _FT_PATH = _DOC_ROOT_PATH + "ft/";
    static final String _FR94_PATH = _DOC_ROOT_PATH + "fr94/";
    static final String _FBIS_PATH = _DOC_ROOT_PATH + "fbis/";
    static final String _LATIMES_PATH = _DOC_ROOT_PATH + "latimes/";

    public Indexer(Directory indexDirectory, String transformerModel) throws Exception {
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        this.iwriter = new IndexWriter(indexDirectory, config);
        this.db = new DbInterface();
        this.transformerModel = transformerModel;
    }

    // This function commits all writes in the index and closes it gracefully.
    public void shutDown() throws IOException {
        this.iwriter.close();
        return;
    }

    // This function reads all document embeddings from the database
    // and creates an index using these embeddings.
    public void readAndIndexDocuments() throws Exception {

        this.db.startSession();

        Embedding currEmbedding;
        List<String> docIds = this.db.getDocumentIds();
        for (String docId : docIds) {
            currEmbedding = this.db.getEmbedding(docId, this.transformerModel);
            Document luceneDocument = new Document();
            luceneDocument.add(new StringField("id", currEmbedding.id, Field.Store.YES));
            luceneDocument.add(new KnnVectorField("body", currEmbedding.embeddingFloat, VectorSimilarityFunction.EUCLIDEAN));
            this.iwriter.addDocument(luceneDocument);
        }
        System.out.println("Finished storing all documents in database");
        this.db.shutDown();
    }

}