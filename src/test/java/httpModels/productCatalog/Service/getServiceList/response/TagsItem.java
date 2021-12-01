package httpModels.productCatalog.Service.getServiceList.response;

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
public class TagsItem{

	@JsonProperty("id")
	private String id;

	@JsonProperty("value")
	private List<String> value;

	@JsonProperty("key")
	private String key;
}