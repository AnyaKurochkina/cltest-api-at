package httpModels.productCatalog.orgDirection.getOrgDirectionList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraData{

	@JsonProperty("test")
	private Boolean test;
}