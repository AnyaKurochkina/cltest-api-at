package models.t1.cdn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Origin {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("source")
    private String source;
    @JsonProperty("backup")
    private Boolean backup;
    @JsonProperty("enabled")
    private Boolean enabled;
}
