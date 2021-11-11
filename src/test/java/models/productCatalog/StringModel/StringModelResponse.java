package models.productCatalog.StringModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StringModelResponse{

	@JsonProperty("service_account")
	private ServiceAccount serviceAccount;
}