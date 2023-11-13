/**
 * A Doc object stores all possible attributes of a document.
 * These attributes may be left blank ("") if there is no corresponding tag in the document.
 * Alternatively, they can be adapted from other tags - e.g. `summary` using the first paragraph of the `body`.
 */


public class Doc {
    String id;
    String title;
    String subtitle;
    String summary;
    String body;

    public Doc(String id, String title, String subtitle, String summary, String body)
    {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.summary = summary;
        this.body = body;
    }

    // Test printing function.
    public void print() {
        System.out.printf("ID: %s\n",this.id);
        System.out.printf("TITLE: %s\n",this.title);
        System.out.printf("SUBTITLE: %s\n",this.subtitle);
        System.out.printf("SUMMARY: %s\n",this.summary);
        System.out.printf("BODY: %s\n",this.body);
    }
}
