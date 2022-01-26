package httpModels.productCatalog.product.existProduct.response;

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
public class ExistProductResponse implements ExistImpl {

	@JsonProperty("exists")
	private Boolean exists;

	@Override
	public boolean isExist() {
		return exists;
	}
}