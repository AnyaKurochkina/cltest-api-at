package models.feedService.eventType;

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
public class GetEventTypeList {

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("list")
	private List<EventType> list;
}