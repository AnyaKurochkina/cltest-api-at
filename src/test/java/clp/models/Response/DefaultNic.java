package clp.models.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultNic{

	@JsonProperty("net_segment")
	private String netSegment;

	public void setNetSegment(String netSegment){
		this.netSegment = netSegment;
	}

	public String getNetSegment(){
		return netSegment;
	}
}