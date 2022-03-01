package httpModels.productCatalog.itemVisualItem.createVisualTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompactTemplate{

	@JsonProperty("name")
	private Name name;

	@JsonProperty("type")
	private Type  type;

	@JsonProperty("status")
	private Status status;
}