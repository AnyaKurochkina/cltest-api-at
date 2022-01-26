package httpModels.productCatalog.action.exportAction.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportActionResponse{

	@JsonProperty("err")
	private String err;

	@JsonProperty("data")
	private String data;
}