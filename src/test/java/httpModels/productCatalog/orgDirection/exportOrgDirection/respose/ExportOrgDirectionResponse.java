package httpModels.productCatalog.orgDirection.exportOrgDirection.respose;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportOrgDirectionResponse{

	@JsonProperty("err")
	private String err;

	@JsonProperty("data")
	private String data;
}