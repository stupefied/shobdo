package objects;

/**
 * Created by tahsinkabir on 6/16/16.
 */
public class BaseWord {

    private String wordId;
    private String wordSpelling;
    private int timesSearched;
    private String linkToPronunciation;

    public BaseWord(String wordId, String wordSpelling) {
        this.wordId = wordId;
        this.wordSpelling = wordSpelling;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getWordSpelling() {
        return wordSpelling;
    }

    public void setWordSpelling(String wordSpelling) {
        this.wordSpelling = wordSpelling;
    }

    public int getTimesSearched() {
        return timesSearched;
    }

    public void setTimesSearched(int timesSearched) {
        this.timesSearched = timesSearched;
    }

    @Override
    public String toString() {
        return "BaseWord{" +
                "wordId='" + wordId + '\'' +
                ", wordSpelling='" + wordSpelling + '\'' +
                ", timesSearched=" + timesSearched +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseWord baseWord = (BaseWord) o;

        return wordSpelling != null ? wordSpelling.equals(baseWord.wordSpelling) : baseWord.wordSpelling == null;

    }

    @Override
    public int hashCode() {
        return wordSpelling != null ? wordSpelling.hashCode() : 0;
    }
}
