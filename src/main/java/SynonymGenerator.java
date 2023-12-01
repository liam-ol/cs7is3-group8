import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;

import java.util.HashSet;
import java.util.Set;

public class SynonymGenerator {
    
    private static final String WN_CONFIG_PATH = "/home/azureuser/ir2-bm25/cs7is3-group8/WordNet-3.0/dict/";

    public static Set<String> generateSynonyms(String word) {
        Set<String> synonyms = new HashSet<>();

        try {
            // Initialize JWNL
            Dictionary dictionary = Dictionary.getFileBackedInstance(WN_CONFIG_PATH);

            // Get synsets for the word
            IndexWordSet indexWordSet = dictionary.lookupAllIndexWords(word);
            for (POS pos : indexWordSet.getValidPOSSet()) {
                IndexWord indexWord = indexWordSet.getIndexWord(pos);
                for (Synset synset : indexWord.getSenses()) {
                    // Add synonyms from the synset
                    for (net.sf.extjwnl.data.Word synWord : synset.getWords()) {
                        synonyms.add(synWord.getLemma());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return synonyms;
    }
}
