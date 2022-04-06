package httpModels.productCatalog.itemVisualItem.getVisualTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreType
public class DefaultItem{

	@JsonProperty("Key")
	private String key;
}