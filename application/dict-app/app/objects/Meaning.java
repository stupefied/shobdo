package objects;

import lombok.Data;
import org.joda.time.DateTime;
import sun.jvm.hotspot.debugger.win32.coff.COFFLineNumber;
import utilities.Constants;
import utilities.JsonUtil;

import java.util.Date;

/**
 * Created by tahsinkabir on 6/16/16.
 */
@Data
public class Meaning {

    private String meaningId;
    private String meaning;
    private String partOfSpeech;
    private String exampleSentence;

    private String creatorId;
    private Date creationDate;

    //For versioning of the meanings
    private String status = Constants.ENTITIY_ACTIVE;
    private String parentMeaningId; //null for pioneer meaning
    private Date deletedDate;

    //V1.5 validation of updates
    private String validatorId; //if validatorId is present, then the meaning is validated

    //V2 attributes
    private int strength = -1; //how strongly does this meanings apply to the word, -1 means unset
    private int version = 0; //V2 version of the meaning as the object gets updated, can be back populated using parentId chain

    public Meaning() { }

    public Meaning(String partOfSpeech, String meaning, String exampleSentence, int strength) {
        this.partOfSpeech = partOfSpeech;
        this.meaning = meaning;
        this.exampleSentence = exampleSentence;
        this.strength = strength;
    }

    @Override
    public String toString() {
        return JsonUtil.toJsonString(this);
    }
}