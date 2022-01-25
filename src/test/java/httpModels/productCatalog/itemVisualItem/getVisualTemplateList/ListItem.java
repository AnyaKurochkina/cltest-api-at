package httpModels.productCatalog.itemVisualItem.getVisualTemplateList;

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
public class ListItem{

	@JsonProperty("event_type")
	private List<String> eventType;

	@JsonProperty("compact_template")
	private Object compactTemplate;

	@JsonProperty("is_active")
	private Boolean isActive;

	@JsonProperty("full_template")
	private Object fullTemplate;

	@JsonProperty("event_provider")
	private List<String> eventProvider;

	@JsonProperty("name")
	private String name;

	@JsonProperty("default_item")
	private Object defaultItem;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;
}