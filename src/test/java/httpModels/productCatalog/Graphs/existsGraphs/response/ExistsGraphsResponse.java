package httpModels.productCatalog.Graphs.existsGraphs.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.ExistImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistsGraphsResponse implements ExistImpl {

	@JsonProperty("exists")
	private boolean exists;

	public boolean isExists(){
		return exists;
	}

	@Override
	public boolean isExist() {
		return exists;
	}
}