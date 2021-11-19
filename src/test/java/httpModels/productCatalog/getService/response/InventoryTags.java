package httpModels.productCatalog.getService.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InventoryTags{

	@JsonProperty("response_tags")
	private List<ResponseTagsItem> responseTags;

	@JsonProperty("operations_tags")
	private List<String> operationsTags;

	@JsonProperty("exclude_tags")
	private String excludeTags;

	@JsonProperty("tags")
	private List<TagsItem> tags;
}