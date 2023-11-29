import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.EnglishStemmer;

public class CustomAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tstream = new EnglishPossessiveFilter(tokenizer);
        tstream = new LowerCaseFilter(tstream);
        tstream = new TrimFilter(tstream);
        tstream = new StopFilter(tstream, loadStopwords());
        tstream = new SnowballFilter(tstream, new EnglishStemmer());
        return new TokenStreamComponents(tokenizer, tstream);
    }

    private CharArraySet loadStopwords() {
        CharArraySet stopwordSet = new CharArraySet(0, true);
        try (BufferedReader br = new BufferedReader(new FileReader("stopwords.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String currStopword = line.trim();
                if (!currStopword.isEmpty()) {
                    stopwordSet.add(currStopword.toCharArray());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return stopwordSet;
    }

}
