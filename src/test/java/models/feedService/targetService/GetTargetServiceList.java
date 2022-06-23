package models.feedService.targetService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.feedService.Meta;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTargetServiceList {

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("list")
	private List<TargetService> list;
}