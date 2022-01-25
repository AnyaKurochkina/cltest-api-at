package httpModels.productCatalog.service.getServiceList.response;

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
public class InventoryTags{

	@JsonProperty("response_tags")
	private List<ResponseTagsItem> responseTags;

	@JsonProperty("tags")
	private List<TagsItem> tags;

	@JsonProperty("operations_tags")
	private List<Object> operationsTags;

	@JsonProperty("exclude_tags")
	private String excludeTags;

	@JsonProperty("excluding_tags")
	private List<ExcludingTagsItem> excludingTags;
}