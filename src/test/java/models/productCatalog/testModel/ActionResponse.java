package models.productCatalog.testModel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionResponse{

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("list")
	private List<ListItem> list;
}