package clp.models.response.postOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultNic{

	@JsonProperty("net_segment")
	private String netSegment;

	public String getNetSegment(){
		return netSegment;
	}
}