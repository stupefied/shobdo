package daos;

import objects.DictionaryWord;
import sun.security.util.DisabledAlgorithmConstraints;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by tahsinkabir on 8/14/16.
 */
public interface WordDao {

    public String setDictionaryWord(DictionaryWord dictionaryWord);

    public DictionaryWord getDictionaryWordByWordId(String wordId);

    public DictionaryWord getDictionaryWordBySpelling(String spelling);

    public Set<String> getWordSpellingsWithPrefixMatch(String wordSpelling, int limit); //returns the spelling of the words that matches

    public long totalWordCount();

    public void deleteAllWords();

}
