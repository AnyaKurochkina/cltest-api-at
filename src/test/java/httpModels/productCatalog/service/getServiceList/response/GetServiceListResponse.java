package httpModels.productCatalog.service.getServiceList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.MetaImpl;

import java.util.List;


public class GetServiceListResponse implements GetListImpl {

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("list")
	private List<ListItem> list;

	@Override
	public List getItemsList() {
		return list;
	}

	@Override
	public MetaImpl getMeta() {
		return meta;
	}
}