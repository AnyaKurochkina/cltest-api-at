package models.t1.dns;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.util.List;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class DnsZone {
    private String description;
    private String name;
    private String type;
    @JsonProperty("domain_name")
    private String domainName;
    @JsonProperty("order_id")
    private String orderId;
    private String project_id;
    private List<String> networks;
    private String id;
    private String created_at;
    private String updated_at;
    private Object created_by;
    private Object updated_by;
    private List<Object> external_id;
    private List<Object> rrsets;


    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("t1/createZone.json")
                .set("$.name", name)
                .set("$.description", description)
                .set("$.type", type)
                .set("$.domain_name", domainName)
                .set("$.order_id", orderId)
                .set("$.networks", networks)
                .build();
    }

}
