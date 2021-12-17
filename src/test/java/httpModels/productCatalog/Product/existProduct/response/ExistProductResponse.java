package httpModels.productCatalog.Product.existProduct.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistProductResponse{

	@JsonProperty("exists")
	private Boolean exists;
}