package httpModels.productCatalog.Graphs.existsGraphs.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistsGraphsResponse{

	@JsonProperty("exists")
	private boolean exists;

	public boolean isExists(){
		return exists;
	}
}