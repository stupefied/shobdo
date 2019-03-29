package utilities;

import objects.UserRequest;
import objects.Word;
import objects.Meaning;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Tahsin Kabir on 8/27/16.
 */
public class DictUtil {

    private static final LogPrint log = new LogPrint(DictUtil.class);

    /* package private */ static int randomIntInRange(final int lowest, final int highest) {
        return new Random().nextInt(highest - lowest + 1) + lowest;
    }

    public static UserRequest getUserRequestFromDocument(final Document dictionaryDocument, final Class<?> class_type) {
        dictionaryDocument.remove("_id");
        return (UserRequest) JsonUtil.documentToObject(dictionaryDocument, class_type);
    }

    public static Word getWordFromDocument(final Document dictionaryDocument, final Class<?> class_type) {
        dictionaryDocument.remove("_id");
        return (Word) JsonUtil.documentToObject(dictionaryDocument, class_type);
    }

    public static Set<Meaning> generateRandomMeaning(final String wordSpelling, final int numberOfMeanings){
        final Set<Meaning> meanings = new HashSet<>();
        for (int i = 0 ; i < numberOfMeanings ; i++) {
            Meaning meaning = generateARandomMeaning(wordSpelling);
            meanings.add(meaning);
        }
        return meanings;
    }

    private static Meaning generateARandomMeaning(final String wordSpelling) {
        final String meaningString =  BanglaUtil.generateRandomSentence(3);
        final String exampleSentence =  BanglaUtil.generateSentenceWithWord(wordSpelling);
        return Meaning.builder()
            .meaning(meaningString)
            .exampleSentence(exampleSentence)
            .build();
    }

    public static Set<Word> generateRandomWordSet(int numberOfWords){
        final Set<Word> words = new HashSet<>();
        for (int i = 0 ; i < numberOfWords ; i++) {
            final Word word = generateARandomWord();
            words.add(word);
        }
        return words;
    }

    private static Word generateARandomWord() {
        final int wordLength = randomIntInRange(2, 9);
        return Word.builder()
            .wordSpelling(BanglaUtil.generateRandomWord(wordLength))
            .build();
    }

    public static Word generateARandomWord(final String partsOfSpeech ) {
        int wordLength = randomIntInRange(2, 9);
        final String wordSpelling = BanglaUtil.generateRandomWord(wordLength);

        final HashMap<String,Meaning> meaningsMap = new HashMap<>();
        int numberOfMeaningForPOS = randomIntInRange(1,3);

        for(int j = 0; j < numberOfMeaningForPOS ; j++) {
            wordLength = randomIntInRange(2, 9);
            String meaningString = BanglaUtil.generateRandomWord(wordLength);
            String exampleSentence = BanglaUtil.generateSentenceWithWord(meaningString);
            int strength = randomIntInRange(0 , 10);
            //Meaning meaning = new Meaning(partOfSpeech, meaningString, exampleSentence, strength);
            //meaningsMap.put(meaning.getId(), meaning);
        }

        return Word.builder()
            .wordSpelling(wordSpelling)
            .build();
    }

    public static void printStringsByTag(String tag, List<?> strings, int start, int limit, boolean randomize) {

        if (strings == null)
            return;

        List<?> toPrint = strings;

        if (randomize) {
            toPrint = new ArrayList<>(strings);
            Collections.shuffle(toPrint);
        }

        for(int i = start ; i < toPrint.size() && i <  start + limit ; i++) {
            log.info("#" + i + " " + tag + ": '"+ toPrint.get(i).toString() + "'");
        }
    }

    public static Map<String, Word> removeKeyValuesForKeys(Map<String, Word> map, Set<String> keys) {
        return map.entrySet().stream()
            .filter(e -> !keys.contains(e.getKey() ) )
            .collect(
                Collectors.toMap(
                    e -> e.getKey(),
                    e -> e.getValue()
                )
            );
    }

    public static Map<String, Word> filterForKeys(Map<String, Word> map, Set<String> keys) {
        return map.entrySet().stream()
            .filter(e -> keys.contains(e.getKey()))
            .collect(
                Collectors.toMap(
                    e->e.getKey(),
                    e->e.getValue()
                )
            );
    }
}
