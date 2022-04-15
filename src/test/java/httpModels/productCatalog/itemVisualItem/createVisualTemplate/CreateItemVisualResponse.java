package httpModels.productCatalog.itemVisualItem.createVisualTemplate;

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
public class CreateItemVisualResponse{

	@JsonProperty("event_type")
	private List<Object> eventType;

	@JsonProperty("compact_template")
	private CompactTemplate compactTemplate;

	@JsonProperty("is_active")
	private Boolean isActive;

	@JsonProperty("full_template")
	private FullTemplate fullTemplate;

	@JsonProperty("event_provider")
	private List<Object> eventProvider;

	@JsonProperty("name")
	private String name;

	@JsonProperty("default_item")
	private DefaultItem defaultItem;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("create_dt")
	private String create_dt;

	@JsonProperty("update_dt")
	private String update_dt;
}