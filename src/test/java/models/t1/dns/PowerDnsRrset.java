package models.t1.dns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerDnsRrset {

    @JsonProperty("comments")
    private List<Object> comments;

    @JsonProperty("records")
    private List<RecordsItem> records;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("ttl")
    private int ttl;
}