package utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import objects.DictionaryWord;
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

    public static DictionaryWord getDictionaryWordFromDocument(Document dictionaryDocument, Class<?> class_type) {

        dictionaryDocument.remove("_id");
        return (DictionaryWord) getObjectFromDocument(dictionaryDocument, class_type);
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

    public static Set<DictionaryWord> generateDictionaryWithRandomWords(int numberOfWords){

        Set<DictionaryWord> words = new HashSet<>();

        for(int i = 0 ; i < numberOfWords ; i++) {

            DictionaryWord word = generateARandomWord( new PartsOfSpeechSet() );
            words.add(word);
        }

        return words;
    }

    public static DictionaryWord generateARandomWord(PartsOfSpeechSet partsOfSpeech ) {

        String start = "995"; //ক
        String end = "9A8";   //ন

        String wordSpelling;
        String wordId;

        int wordLength = DictUtil.randomInRange(2, 9);
        wordSpelling = Bangla.getWord(start, end, wordLength);
        wordId = "WD_" + UUID.randomUUID();

        DictionaryWord dictionaryWord = new DictionaryWord(wordId, wordSpelling);

        ArrayList<Meaning> meanings = new ArrayList<>();

        for (String pos : partsOfSpeech.getPartsOfSpeeches()) {

            int numberOfMeaningForPOS = DictUtil.randomInRange(1,3);

            for(int j = 0; j < numberOfMeaningForPOS ; j++) {

                wordLength = DictUtil.randomInRange(2, 9);

                String meaningString = Bangla.getWord(start, end, wordLength);
                int preSentenceLen = DictUtil.randomInRange(2, 6);
                int postSentenceLen = DictUtil.randomInRange(2, 4);
                String example = Bangla.getSentence(start, end, preSentenceLen, 12);
                example += " " + meaningString + " ";
                example += Bangla.getSentence(start, end, postSentenceLen, 12);

                String meaningId = "MN_" + UUID.randomUUID();

                int strength = DictUtil.randomInRange(0 , 10);
                Meaning meaning = new Meaning(meaningId, pos, meaningString, example, strength);

                meanings.add(meaning);
            }
        }

        dictionaryWord.setMeanings(meanings);

        return dictionaryWord;
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

    public static Map<String, DictionaryWord> removeKeyValuesForKeys(Map<String,DictionaryWord> map, Set<String> keys) {

        return map.entrySet().stream()
                .filter( e-> !keys.contains( e.getKey() ) )
                .collect(
                        Collectors.toMap(
                                e -> e.getKey(),
                                e -> e.getValue()
                        )
                );
    }

    public static Map<String,DictionaryWord> filterForKeys(Map<String,DictionaryWord> map, Set<String> keys) {

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
