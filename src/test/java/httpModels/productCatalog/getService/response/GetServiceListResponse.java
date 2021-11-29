package httpModels.productCatalog.getService.response;

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

public class GetServiceListResponse{

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("list")
	private List<ListItem> list;
}