// The Topic class holds all information about a topic.
public class Topic {
    int id;
    String title;
    String description;
    String narrative;

    public Topic(int id, String title, String description, String narrative) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.narrative = narrative;
    }
}