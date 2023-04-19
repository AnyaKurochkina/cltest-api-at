package models.cloud.stateService.extRelations;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import org.json.JSONObject;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ExtRelation{

	@JsonProperty("primary_item_id")
	private String primaryItemId;

	@JsonProperty("secondary_item_id")
	private String secondaryItemId;

	@JsonProperty("is_exclusive")
	private Boolean isExclusive;

	@JsonProperty("create_dt")
	private String createDt;

	private Integer id;

	public JSONObject toJson() {
		return JsonHelper.getJsonTemplate("stateService/createExtRelation.json")
				.set("$.primary_item_id", primaryItemId)
				.set("$.secondary_item_id", secondaryItemId)
				.set("$.is_exclusive", isExclusive)
				.build();
	}
}