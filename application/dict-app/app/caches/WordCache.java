package caches;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.ConfigFactory;
import objects.Word;
import redis.clients.jedis.Jedis;
import utilities.*;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public final class WordCache {

    /* Redis expire time */
    private static final boolean USE_REDIS_EXPIRATION_TIME = true;
    private static final int REDIS_EXPIRE_TIME_SECONDS= 60 * 60 * 6;

    private static final LogPrint log = new LogPrint(WordCache.class);
    private final Jedis jedis;

    public WordCache() {
        final String DEFAULT_REDIS_HOSTNAME = ConfigFactory.load().getString("shobdo.redishostname");
        log.info("@WC001 Connect to redis [host:" +  DEFAULT_REDIS_HOSTNAME + "][port:6379]." );
        jedis = new Jedis(DEFAULT_REDIS_HOSTNAME);
    }

    public Word getBySpelling(final String spelling) throws IOException {
        if (spelling == null) {
            return null;
        }
        final String key = getKeyForSpelling(spelling);
        final String wordJsonString = jedis.get(key);
        if (wordJsonString != null) {
            try {
                log.info("@WC003 Word [" + spelling + "] found in cache and returning");
                return new ObjectMapper().readValue(wordJsonString, Word.class);
            } catch (IOException ex) {
                return null;
            }
        }
        return null;
    }

    public void cacheWord(final Word word) {
        if (word == null) {
            return;
        }
        final String key = getKeyForSpelling(word.getWordSpelling());
        jedis.set(key, word.toString());
        if (USE_REDIS_EXPIRATION_TIME) {
            jedis.expire(key, REDIS_EXPIRE_TIME_SECONDS);
        }
        log.info("@WC004 Word [" + word.getWordSpelling() + "] stored in cache.");
    }

    public void invalidateWord(final Word word) {
        if (word == null) {
            return;
        }
        final String key = getKeyForSpelling(word.getWordSpelling());
        try {
            jedis.del(key);
            log.info("@WC004 Word [" + word.getWordSpelling() + "] cleared from cache.");
        } catch (Exception ex) {
            log.info("@WC007 Error while storing JSON string of word");
        }
    }

    public Set<String> getWordsForSearchString(final String searchString){
        if (searchString == null) {
            return null;
        }
        final String key = getKeyForSearchString(searchString);
        final Set<String> words = jedis.smembers(key);
        if (words != null && words.size() > 0) {
            log.info("@WC005 Search result found and returning from cache. Count: " + words.size() + ".");
            return words;
        } else {
            log.info("@WC005 Search result not found on cache for spelling: \'" + searchString + "\'");
            return null;
        }
    }

    public void cacheWordsForSearchString(final String searchString, final Set<String> words) {
        if (searchString == null || words == null || words.size() == 0) {
            return;
        }
        final String key = getKeyForSearchString(searchString);
        final String wordsString = String.join("-|-",
                words.stream().map(w->toString()).collect(Collectors.toList())
        );
        jedis.set(key, wordsString);
        if (USE_REDIS_EXPIRATION_TIME) {
            jedis.expire(key, REDIS_EXPIRE_TIME_SECONDS);
        }
        log.info("@WC006 Storing search results on cache. Count: " + words.size() + ".");
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