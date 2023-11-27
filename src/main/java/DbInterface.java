import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.json.JSONArray;
import org.json.JSONObject;

public class DbInterface {

    private SessionFactory sfactory;
    private Session currSession;
    private Transaction currTransaction;
    private Integer queriesInTransaction;
    private final Integer _MAX_QUERIES_IN_TRANSACTION = 25;

    public DbInterface() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        StandardServiceRegistry sRegistry = new StandardServiceRegistryBuilder().applySettings(
            configuration.getProperties()
        ).build();
        this.sfactory = configuration.buildSessionFactory(sRegistry);
        this.currSession = null;
    }

    public void startSession() {
        if (this.currSession == null) {
            this.currSession = this.sfactory.openSession();
            this.currTransaction = this.currSession.beginTransaction();
            this.queriesInTransaction = 0;
        }
    }

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

    public Embedding getEmbedding(String docId) {
        this.startSession();
        String sql = "SELECT id, embedding FROM embeddings where id = ?;";
        NativeQuery<Embedding> query = this.currSession.createNativeQuery(sql, Embedding.class);
        query.setParameter(1, docId);
        Embedding embedding = query.getSingleResultOrNull();
        embedding.convertToFloat();
        return embedding;
    }

    public void shutDown() {
        this.currTransaction.commit();
        System.out.println("Transaction completed");
        this.currSession.close();
        this.sfactory.close();
    }
}
