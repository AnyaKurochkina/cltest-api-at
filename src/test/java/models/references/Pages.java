package models.references;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.List;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Pages {

    private String name;
    private String id;
    private JSONObject data;
    @JsonProperty("directory")
    private String directoryId;
    private List<String> tags;
    private Integer weight;
}
