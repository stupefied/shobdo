package cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import objects.DictionaryWord;
import redis.clients.jedis.Jedis;
import utilities.LogPrint;

import java.util.Set;

public class WordCache {

    private static boolean USE_REDIS = true;
    private static final String DEFAULT_REDIS_HOSTNAME = "localhost";

    private Jedis jedis;

    /* Redis key words */
    private final String SERACH_WORD_BY_SPELLING_PFX = "SWBS_";
    private final String GET_WORD_BY_SPELLING_PFX = "GWBS_";

    /*Redis expire time*/
    private boolean USE_REDIS_EXPIRATION_TIME = false;
    private final int REDIS_EXPIRE_TIME = 10; //in seconds

    private LogPrint log = new LogPrint(WordCache.class);

    public WordCache() {

        jedis = getJedis(getHostname());

    }

    public String getHostname() { //You may return environment from here

        return DEFAULT_REDIS_HOSTNAME;
    }

    public Jedis getJedis(String hostname) {

        Jedis jedis = null;

        try {

            jedis = new Jedis(hostname);

        } catch (Exception ex) {

            log.info("Exception occurred while connecting to Redis. Message:" + ex.getMessage());
        }

        return jedis;
    }

    public DictionaryWord getDictionaryWordBySpellingFromCache(String spelling) {

        if ( (!USE_REDIS) || jedis == null || spelling == null)
            return null;

        String key = GET_WORD_BY_SPELLING_PFX + spelling;

        String wordJsonString = jedis.get(key);

        if (wordJsonString != null) {

            log.debug("Word [" + spelling + "] found and returning from redis.");

            ObjectMapper mapper = new ObjectMapper();

            try {

                return mapper.readValue(wordJsonString, DictionaryWord.class);

            } catch (Exception ex) {

                log.info("Error converting jsonString to Object. Exception:" + ex.getStackTrace().toString());
            }
        }

        return null;
    }

    public void cacheDictionaryWord(DictionaryWord word) {

        if ( (!USE_REDIS) || jedis == null || word == null)
            return;

        String key = getKeyForSpelling(word.getWordSpelling());

        try {

            jedis.set(key, word.toJsonString());

            if (USE_REDIS_EXPIRATION_TIME)
                jedis.expire(key, REDIS_EXPIRE_TIME);

        } catch (Exception ex){

            log.info("Error while storing JSON string of word");
        }
    }

    public Set<String> getSearchWordsBySpellingFromCache(String spelling){

        if ( (!USE_REDIS) || jedis == null || spelling == null)
            return null;

        String key = getKeyForSearch(spelling);

        Set<String> words = jedis.smembers(key);

        if( words != null && words.size() > 0) {
            log.debug("Search result found and returning from redis. Count: " + words.size());
            return words;
        }

        return null;
    }

    public void cacheSearchWordsBySpelling(String spelling, Set<String> words){

        if ( (!USE_REDIS) || jedis == null || spelling == null)
            return;

        String key = getKeyForSearch(spelling);

        for(String word: words) {
            jedis.sadd(key, word);
        }

        if(USE_REDIS_EXPIRATION_TIME)
            jedis.expire( key, REDIS_EXPIRE_TIME);

    }

    public String getKeyForSpelling(String spelling){
        return GET_WORD_BY_SPELLING_PFX + spelling;
    }

    public String getKeyForSearch(String spelling){
        return SERACH_WORD_BY_SPELLING_PFX + spelling;
    }
}