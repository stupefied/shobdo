package objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.Objects;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Antonym {

    private String spelling;

    //wordId of the word which has the antonym as its spelling
    @JsonIgnore //Ignore to not expose to API
    private String targetWordId;

    //determines how strong of an antonym it is
    private int strength;

    public JsonNode toAPIJsonNode() {
        return new ObjectMapper().convertValue(this, JsonNode.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Antonym antonym = (Antonym) o;
        return spelling.equals(antonym.spelling);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spelling);
    }
}
