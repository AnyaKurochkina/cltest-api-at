package httpModels.productCatalog.service.getServiceList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTagsItem{

	@JsonProperty("name")
	private String name;

	@JsonProperty("column_name")
	private String columnName;
}