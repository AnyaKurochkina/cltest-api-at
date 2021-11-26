package httpModels.productCatalog.OrgDirection.getOrgDirectionList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meta{

	@JsonProperty("next")
	private Object next;

	@JsonProperty("previous")
	private Object previous;

	@JsonProperty("total_count")
	private Integer totalCount;
}