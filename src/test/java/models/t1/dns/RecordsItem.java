package models.t1.dns;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecordsItem{

	@JsonProperty("disabled")
	private boolean disabled;

	@JsonProperty("content")
	private String content;
}