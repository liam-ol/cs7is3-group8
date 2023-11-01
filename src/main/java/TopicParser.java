import java.util.ArrayList;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class TopicParser {

    private class Topic {
        String title;
        String description;
        String narrative;
    }

    private ArrayList<Topic> readTopics() {

        String _TOPICS_FILE = "/home/azureuser/cs7is3-group8/data/topics";

        String line;
        ArrayList<Topic> topics = new ArrayList<Topic>();
        Topic top = new Topic();
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(_TOPICS_FILE);
            br = new BufferedReader(fr);
            line = br.readLine();
            while (line != null) {
                line = br.readLine();
            }
            System.out.println("All lines read!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return topics;
    }

}