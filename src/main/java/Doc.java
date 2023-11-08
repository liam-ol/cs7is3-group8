/**
 * A Doc object stores all possible attributes of a document.
 * These attributes may be left blank ("") if there is no corresponding tag in the document.
 * Alternatively, they can be adapted from other tags - e.g. `summary` using the first paragraph of the `body`.
 */


public class Doc {
    int id;
    String title;
    String subtitle;
    String summary;
    String body;

    public Doc(int id, String title, String subtitle, String summary, String body)
    {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.summary = summary;
        this.body = body;
    }
}
