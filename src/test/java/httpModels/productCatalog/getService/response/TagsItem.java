package httpModels.productCatalog.getService.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TagsItem{

	@JsonProperty("id")
	private String id;

	@JsonProperty("value")
	private List<String> value;
}