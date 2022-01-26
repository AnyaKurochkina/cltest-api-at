package httpModels.productCatalog.graphs.deleteGraph.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteGraphResponse{

	@JsonProperty("err")
	private String err;
}