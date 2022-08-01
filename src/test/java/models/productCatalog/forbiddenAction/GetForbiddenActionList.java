package models.productCatalog.forbiddenAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.productCatalog.Meta;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetForbiddenActionList {

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("list")
	private List<ForbiddenAction> list;
}