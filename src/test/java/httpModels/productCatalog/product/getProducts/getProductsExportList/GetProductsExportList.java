package httpModels.productCatalog.product.getProducts.getProductsExportList;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.MetaImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductsExportList implements GetListImpl {

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("list")
	private List<ExportItem> list;

	@Override
	public List getItemsList() {
		return list;
	}

	@Override
	public MetaImpl getMeta() {
		return meta;
	}
}