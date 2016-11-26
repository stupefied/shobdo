package daoImplementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import daos.WordDao;
import objects.DictionaryWord;
import org.bson.Document;
import utilities.DictUtil;
import utilities.LogPrint;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * Created by tahsinkabir on 8/14/16.
 */
public class WordDaoMongoImpl implements WordDao {

    public final String DICTIONARY_DATABASE_NAME = "Dictionary";
    public final String WORD_COLLECTION_NAME = "Words";

    private final String WORD_ID = "wordId";
    private final String WORD_SPELLING = "wordSpelling";

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> collection;

    private LogPrint log = new LogPrint(WordDaoMongoImpl.class);

    public WordDaoMongoImpl(){

        mongoClient = new MongoClient( "localhost" , 27017 );
        //mongoClient = new MongoClient( "172.17.0.1" , 27017 );
        mongoDatabase = mongoClient.getDatabase(DICTIONARY_DATABASE_NAME);
        collection = mongoDatabase.getCollection(WORD_COLLECTION_NAME);

    }

    @Override
    public String setDictionaryWord(DictionaryWord dictionaryWord) {

        log.info("Saving word to database: " + dictionaryWord.toString());

        try {

            ObjectMapper mapper = new ObjectMapper();
            Document wordDocument = Document.parse( mapper.writeValueAsString(dictionaryWord) );
            log.info(wordDocument.toString());
            collection.insertOne(wordDocument);

            //String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dictionaryWord);
            //log.info("Json String for Dictionary Word: " + jsonString);

        } catch ( Exception ex ){

            log.info( "Failed to map dictionary word object to jsonString. Ex: " + ex.getMessage() );

        }

        return null;
    }

    @Override
    public DictionaryWord getDictionaryWordByWordId(String wordId) {

        BasicDBObject query = new BasicDBObject(WORD_ID, wordId);

        Document word = collection.find(query).first();

        if(word == null)
            return null;

        DictionaryWord dictionaryWord = (DictionaryWord) DictUtil
                .getObjectFromDocument( word, DictionaryWord.class);

        return dictionaryWord;
    }

    @Override
    public DictionaryWord getDictionaryWordBySpelling(String spelling) {

        BasicDBObject query = new BasicDBObject(WORD_SPELLING, spelling);

        Document word = collection.find(query).first();

        if(word == null)
            return null;

        DictionaryWord dictionaryWord = (DictionaryWord) DictUtil
                .getObjectFromDocument( word, DictionaryWord.class);

        return dictionaryWord;

    }

    @Override
    public Set<String> getWordsWithPrefixMatch(String spelling, int limit) {

        final String WORD_SPELLING = "wordSpelling";

        Pattern prefixForSpellPattern = Pattern.compile("^" + spelling + ".*");

        BasicDBObject query = new BasicDBObject(WORD_SPELLING, prefixForSpellPattern);

        MongoCursor<Document> words = collection
                .find(query)
                .projection(Projections.include(WORD_SPELLING))
                .limit(limit)
                .batchSize(100)
                .iterator();

        Set<String> result = new HashSet<>();

        while(words.hasNext()) {

            Document bleh = words.tryNext();
            result.add( bleh.get(WORD_SPELLING).toString() );

        }

        return result;
    }

    @Override
    public long totalWordCount() {
        return collection.count();
    }

}
