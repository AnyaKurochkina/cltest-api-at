package httpModels.productCatalog.template.getListTemplate.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.MetaImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Meta implements MetaImpl {

	@JsonProperty("next")
	private String next;

	@JsonProperty("previous")
	private String previous;

	@JsonProperty("total_count")
	private Integer totalCount;

	@Override
	public String getNext() {
		return next;
	}

	@Override
	public String getPrevious() {
		return null;
	}

	@Override
	public Integer getTotalCount() {
		return totalCount;
	}

}