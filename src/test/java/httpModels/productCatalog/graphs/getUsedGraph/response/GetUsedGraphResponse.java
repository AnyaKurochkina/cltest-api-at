package httpModels.productCatalog.graphs.getUsedGraph.response;

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
public class GetUsedGraphResponse{

	@JsonProperty("GetUsedGraphResponse")
	private List<GetUsedGraphResponseItem> getUsedGraphResponse;
}