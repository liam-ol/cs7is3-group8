import java.util.ArrayList;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.lang.StringBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class TopicParser {

    public ArrayList<Topic> topics;
    private String _TOPICS_FILE = "./topics";
    
    // readTopics function reads the topics from a file and stores them in an ArrayList.
    private ArrayList<Topic> readTopics() {

        ArrayList<Topic> topics = new ArrayList<Topic>();
        
        String line;
        FileReader fr = null;
        BufferedReader br = null;
        StringBuilder topicText = null;

        try {
            // Open the topics file.
            fr = new FileReader(_TOPICS_FILE);
            br = new BufferedReader(fr);

            // Read the file line to line.
            line = br.readLine();
            while (line != null) {
                // For every topics read all lines and save them in a string variable.
                if (line.contains("<top>")) {
                    topicText = new StringBuilder();
                    line = br.readLine();
                    while (!line.contains("</top>")) {
                        if (line != "\n") {
                            topicText.append(line.trim() + " ");
                        }
                        line = br.readLine();
                    }

                    // Isolate number, title, description and narrative using a regular expression.
                    Pattern pattern = Pattern.compile("^\\s*<num>\\s*Number:\\s*(?<number>\\d+)\\s*<title>(?<title>.*?)<desc>\\s*Description:(?<description>.*?)<narr>\\s*Narrative:(?<narrative>.*)");
                    Matcher matcher = pattern.matcher(topicText.toString());

                    // Save the topic info in an instance of Topic class and add them to the topics list.
                    if (matcher.find()) {
                        int id = Integer.parseInt(matcher.group("number"));
                        String title = matcher.group("title").trim();
                        String descr = matcher.group("description").trim();
                        String narr = matcher.group("narrative").trim();
                        Topic currTopic = new Topic(id, title, descr, narr);
                        topics.add(currTopic);
                    }
                }
                line = br.readLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            // Close the file gracefully.
            try {
                br.close();
                fr.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return topics;
    }

    public TopicParser() {
        this.topics = readTopics();
    }

}