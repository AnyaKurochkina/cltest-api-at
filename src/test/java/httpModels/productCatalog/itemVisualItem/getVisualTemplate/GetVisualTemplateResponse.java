package httpModels.productCatalog.itemVisualItem.getVisualTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetVisualTemplateResponse implements GetImpl {

	@JsonProperty("event_type")
	private List<String> eventType;

	@JsonProperty("compact_template")
	private CompactTemplate compactTemplate;

	@JsonProperty("is_active")
	private Boolean isActive;

	@JsonProperty("full_template")
	private FullTemplate fullTemplate;

	@JsonProperty("event_provider")
	private List<String> eventProvider;

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

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public String getGraphVersionCalculated() {
		return null;
	}
}