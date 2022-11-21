package models.cloud.productCatalog.visualTeamplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.cloud.productCatalog.Meta;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetVisualTemplateList {

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("list")
	private List<ItemVisualTemplate> list;
}