package models.t1.dns;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rrset {

	@JsonProperty("records")
	private Object records;
	@JsonProperty("record_ttl_seconds")
	private Integer recordTtlSeconds;
	@JsonProperty("record_name")
	private String recordName;
	@JsonProperty("id")
	private String id;
	@JsonProperty("record_type")
	private String recordType;
	private String created_at;
	private String updated_at;
	private Object created_by;
	private Object updated_by;
	private Object zone;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("t1/createRrset.json")
                .set("$.records", records)
                .set("$.record_ttl_seconds", recordTtlSeconds)
                .set("$.record_name", recordName)
                .set("$.record_type", recordType)
                .build();
    }
}