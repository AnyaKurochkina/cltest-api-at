package models.productCatalog.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTypeProvider {
    @JsonProperty("event_type")
    private String event_type;
    @JsonProperty("event_provider")
    private String event_provider;
}
