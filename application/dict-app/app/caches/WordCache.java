package caches;

import com.typesafe.config.ConfigFactory;
import objects.Word;
import redis.clients.jedis.Jedis;
import utilities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class WordCache {

    /* Redis expire time */
    private static final boolean USE_REDIS_EXPIRATION_TIME = true;
    private static final int REDIS_EXPIRE_TIME_SECONDS= 60 * 60 * 6;

    private static final ShobdoLogger log = new ShobdoLogger(WordCache.class);
    private static Jedis jedis;

    private WordCache() {
    }

    public static WordCache getCache() {
        if (jedis == null) {
            final String DEFAULT_REDIS_HOSTNAME = ConfigFactory.load().getString("shobdo.redis.hostname");
            log.info("@WC001 Connect to redis [host:" + DEFAULT_REDIS_HOSTNAME + "][port:6379].");
            jedis = new Jedis(DEFAULT_REDIS_HOSTNAME);
        }
        return new WordCache();
    }

    public Word getBySpelling(final String spelling) {
        if (spelling == null) {
            return null;
        }
        final String key = getKeyForSpelling(spelling);
        final String wordJsonString = jedis.get(key);
        if (wordJsonString != null) {
            log.info("@WC003 Word [" + spelling + "] found in cache and returning");
            return (Word) JsonUtil.jStringToObject(wordJsonString, Word.class);
        }
        return null;
    }

    public void cacheWord(final Word word) {
        if (word == null) {
            return;
        }
        final String key = getKeyForSpelling(word.getSpelling());
        jedis.set(key, word.toString());
        if (USE_REDIS_EXPIRATION_TIME) {
            jedis.expire(key, REDIS_EXPIRE_TIME_SECONDS);
        }
        log.info("@WC004 Word [" + word.getSpelling() + "] stored in cache.");
    }

    public void invalidateWord(final Word word) {
        if (word == null) {
            return;
        }
        final String key = getKeyForSpelling(word.getSpelling());
        try {
            jedis.del(key);
            log.info("@WC004 Word [" + word.getSpelling() + "] cleared from cache.");
        } catch (Exception ex) {
            log.info("@WC007 Error while storing JSON string of word");
        }
    }

    public Set<String> getWordsForSearchString(final String searchString){
        if (searchString == null) {
            return null;
        }
        final String key = getKeyForSearchString(searchString);
        final String result = jedis.get(key);
        if (result == null) {
            return null;
        }
        final Set<String> spellings = (Set<String>) JsonUtil.jStringToObject(result, Set.class);

        if (spellings != null && spellings.size() > 0) {
            log.info("@WC005 Search result found and returning from cache. Count: " + spellings.size() + ".");
            return spellings;
        } else {
            log.info("@WC005 Search result not found on cache for spelling: \'" + searchString + "\'");
            return null;
        }
    }

    public void cacheWordsForSearchString(final String searchString, final Set<String> spellings) {
        if (searchString == null || spellings == null || spellings.size() == 0) {
            return;
        }
        final String key = getKeyForSearchString(searchString);
        jedis.set(key, JsonUtil.objectToJString(spellings));
        if (USE_REDIS_EXPIRATION_TIME) {
            jedis.expire(key, REDIS_EXPIRE_TIME_SECONDS);
        }
        log.info("@WC006 Storing search results on cache. Count: " + spellings.size() + ".");
    }

    private String getKeyForSpelling(String spelling) {
        return String.format("SP_%s", spelling);
    }

    private String getKeyForSearchString(String searchString) {
        return String.format("SS_%s", searchString);
    }

    public void flushCache(){
        jedis.flushAll();
    }
}
