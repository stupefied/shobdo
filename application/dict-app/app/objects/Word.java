package objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import utilities.JsonUtil;
import java.util.*;

/**
 * Created by tahsinkabir on 8/21/16.
 */
@Data
public class Word {

    private String id;
    private String wordSpelling;

    private HashMap<String, Meaning> meaningsMap = new HashMap<>(); //Think of this as the document store for meanings
    private ArrayList<String> antonyms = new ArrayList<>(); //antonym wordIds, should it be id mapped to word spellings
    private ArrayList<String> synonyms = new ArrayList<>(); //synonym wordIds, should it be id mapped to word spellings

    private HashMap<String,List<String>> extraMetaMap;
    private EntityMeta entityMeta;

    public Word() {};

    public Word( String id, String wordSpelling) {

        this.id = id;
        this.wordSpelling = wordSpelling;
    }

    public void setExtraMetaValue(String key, String value) {

        setExtraMetaValue(key, Arrays.asList(value));
    }

    public void setExtraMetaValue(String key, List<String> newValues) {

        if(extraMetaMap == null)
            extraMetaMap = new HashMap<>();

        List<String> values = extraMetaMap.get(key);

        if( values == null )
            values = new ArrayList<>();

        values.addAll(newValues);

        extraMetaMap.put(key, values);
    }

    public List<String> retrieveExtraMetaValuesForKey(String key) {

        return extraMetaMap.get(key);
    }

    public void removeExtraMetaValueForKey(String key) {

        removeExtraMetaValueForKeys(Arrays.asList(key));
    }

    public void removeExtraMetaValueForKeys(Collection<String> keys) {

        for(String key: keys) {
            extraMetaMap.remove(key);
        }
    }

    @Override
    public String toString() {
        return JsonUtil.objectToJsonString(this);
    }
}
