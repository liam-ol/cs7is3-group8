import java.io.*;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.document.KnnVectorField;
import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.similarities.Similarity;

public class EmbeddingIndexer {

    private IndexWriter iwriter;
    private DbInterface db;

    public EmbeddingIndexer(Analyzer engineAnalyzer, Similarity engineSimilarity, Directory indexDirectory) throws Exception {
        IndexWriterConfig config = new IndexWriterConfig(engineAnalyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setSimilarity(engineSimilarity);
        this.iwriter = new IndexWriter(indexDirectory, config);
        this.db = new DbInterface();
    }

    // public void indexEmbeddings() throws IOException {
    //     this.db.startSession();
    //     Embedding emb = db.getEmbedding();
    //     while (emb != null) {
    //         Document luceneDocument = new Document();
    //         System.out.println(emb.id);
    //         luceneDocument.add(new KnnVectorField("body", emb.wordEmbedding, VectorSimilarityFunction.DOT_PRODUCT));
    //         iwriter.addDocument(luceneDocument);
    //         return;
    //     }
    //     this.db.shutDown();
    // }

    // This function commits all writes in the index and closes it gracefully.
    public void shutDown() throws IOException {
        this.iwriter.close();
        return;
    }

}
