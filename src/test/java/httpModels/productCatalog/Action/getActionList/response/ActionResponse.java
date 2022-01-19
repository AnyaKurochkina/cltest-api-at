package httpModels.productCatalog.Action.getActionList.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.GetListImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionResponse implements GetListImpl {

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("list")
	private List<ListItem> list;

	@Override
	public List getItemsList() {
		return list;
	}
}