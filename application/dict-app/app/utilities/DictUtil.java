package utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import objects.Word;
import objects.Meaning;
import objects.PartsOfSpeechSet;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tahsinkabir on 8/27/16.
 */
public class DictUtil {

    private static LogPrint log = new LogPrint(DictUtil.class);

    public static int randomInRange(int lowest, int highest) {
        return new Random().nextInt( highest - lowest + 1) + lowest;
    }

    public static Word getWordFromDocument(Document dictionaryDocument, Class<?> class_type) {

        dictionaryDocument.remove("_id");
        return (Word) getObjectFromDocument(dictionaryDocument, class_type);
    }

    public static Object getObjectFromDocument(Document doc, Class<?> class_type) {

        ObjectMapper mapper = new ObjectMapper();
        Object object = null;

        try {

            object = mapper.readValue( doc.toJson(), class_type );

        } catch(Exception ex) {
            log.info( "Failed to map document " + doc + " to " + class_type + " object. Ex: " + ex.getMessage() );
        }

        return object;
    }

    public static Set<Word> generateDictionaryWithRandomWords(int numberOfWords){

        Set<Word> words = new HashSet<>();

        for(int i = 0 ; i < numberOfWords ; i++) {

            Word word = generateARandomWord( new PartsOfSpeechSet() );
            words.add(word);
        }

        return words;
    }

    public static Word generateARandomWord(PartsOfSpeechSet partsOfSpeech ) {

        String start = "995"; //ক
        String end = "9A8";   //ন

        String wordSpelling;
        String wordId;

        int wordLength = DictUtil.randomInRange(2, 9);
        wordSpelling = BanglaUtil.getBanglaRandomString(start, end, wordLength);
        wordId = "WD_" + UUID.randomUUID();

        Word word = new Word(wordId, wordSpelling);

        ArrayList<Meaning> meanings = new ArrayList<>();

        for (String partOfSpeech : partsOfSpeech.getPartsOfSpeeches()) {

            int numberOfMeaningForPOS = DictUtil.randomInRange(1,3);

            for(int j = 0; j < numberOfMeaningForPOS ; j++) {

                wordLength = DictUtil.randomInRange(2, 9);

                String meaningString = BanglaUtil.getBanglaRandomString(start, end, wordLength);
                int preSentenceLen = DictUtil.randomInRange(2, 6);
                int postSentenceLen = DictUtil.randomInRange(2, 4);
                String example = BanglaUtil.getBanglaRandomSentence(start, end, preSentenceLen, 12) + " " + meaningString
                        + " " + BanglaUtil.getBanglaRandomSentence(start, end, postSentenceLen, 12);

                int strength = DictUtil.randomInRange(0 , 10);
                Meaning meaning = new Meaning(partOfSpeech, meaningString, example, strength);

                meanings.add(meaning);
            }
        }

        word.setMeanings(meanings);

        return word;
    }

    public static void printStringsByTag(String tag, List<?> strings, int start, int limit, boolean randomize) {

        if(strings == null)
            return;

        List<?> toPrint = strings;

        if(randomize) {
            toPrint = new ArrayList<>(strings);
            Collections.shuffle(toPrint);
        }

        for(int i = start ; i < toPrint.size() && i <  start + limit ; i++) {
            log.info( "#" + i + " " + tag + ": '"+ toPrint.get(i).toString() + "'");
        }
    }

    public static Map<String, Word> removeKeyValuesForKeys(Map<String, Word> map, Set<String> keys) {

        return map.entrySet().stream()
                .filter( e-> !keys.contains( e.getKey() ) )
                .collect(
                        Collectors.toMap(
                                e -> e.getKey(),
                                e -> e.getValue()
                        )
                );
    }

    public static Map<String, Word> filterForKeys(Map<String, Word> map, Set<String> keys) {

        return map.entrySet().stream()
                .filter( e -> keys.contains(e.getKey()))
                .collect(
                        Collectors.toMap(
                                e->e.getKey(),
                                e->e.getValue()
                        )
                );
    }
}
