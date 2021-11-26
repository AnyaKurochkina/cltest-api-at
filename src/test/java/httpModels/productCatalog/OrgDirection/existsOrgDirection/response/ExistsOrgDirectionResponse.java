package httpModels.productCatalog.OrgDirection.existsOrgDirection.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistsOrgDirectionResponse{

	@JsonProperty("exists")
	private Boolean exists;
}