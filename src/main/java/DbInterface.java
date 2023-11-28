import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;

public class DbInterface {

    private SessionFactory sfactory;
    private Session currSession;
    private Transaction currTransaction;
    private Integer queriesInTransaction;
    private final Integer _MAX_QUERIES_IN_TRANSACTION = 25;

    // This class creates a connection to the SQLite database 
    // using the Hibernate ORM framework.
    public DbInterface() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        StandardServiceRegistry sRegistry = new StandardServiceRegistryBuilder().applySettings(
            configuration.getProperties()
        ).build();
        this.sfactory = configuration.buildSessionFactory(sRegistry);
        this.currSession = null;
    }

    // Start a session to the database.
    public void startSession() {
        if (this.currSession == null) {
            this.currSession = this.sfactory.openSession();
            this.currTransaction = this.currSession.beginTransaction();
            this.queriesInTransaction = 0;
        }
    }

    // Insert a parsed document to the database.
    public void addDocument(Doc doc) {
        String sql = "INSERT INTO docs (id, body, title, subtitle, summary) values (?, ?, ?, ?, ?);";
        NativeQuery<Doc> query = this.currSession.createNativeQuery(sql, Doc.class);
        query.setParameter(1, doc.id);
        query.setParameter(2, doc.body == null ? "" : doc.body);
        query.setParameter(3, doc.title == null ? "" : doc.title);
        query.setParameter(4, doc.subtitle == null ? "" : doc.subtitle);
        query.setParameter(5, doc.summary == null ? "" : doc.summary);
        query.executeUpdate();
        this.queriesInTransaction++;
        if (this.queriesInTransaction >= _MAX_QUERIES_IN_TRANSACTION) {
            this.currTransaction.commit();
            System.out.println("Transaction completed");
            this.currTransaction = this.currSession.beginTransaction();
            this.queriesInTransaction = 0;
        }
    }

    // Retrieve the text embedding for a document from the database.
    public Embedding getEmbedding(String docId, String model) {
        this.startSession();
        //TODO: Change table name
        String sql = "SELECT id, embedding FROM embeddings where id = ?;";
        NativeQuery<Embedding> query = this.currSession.createNativeQuery(sql, Embedding.class);
        query.setParameter(1, docId);
        Embedding embedding = query.getSingleResultOrNull();
        embedding.convertToFloat();
        return embedding;
    }

    // Get a list with the IDs of all documents in the database.
    public List<String> getDocumentIds() {
        this.startSession();
        String sql = "SELECT id FROM embeddings;";
        NativeQuery<String> query = this.currSession.createNativeQuery(sql, String.class);
        List<String> docIds = query.getResultList();
        return docIds;
    }

    // Commit and close database connection gracefully.
    public void shutDown() {
        this.currTransaction.commit();
        System.out.println("Transaction completed");
        this.currSession.close();
        this.sfactory.close();
    }
}
