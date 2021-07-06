package clp.models.response.getOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetSegment{

	@JsonProperty("code")
	private String code;

	@JsonProperty("is_deleted")
	private boolean isDeleted;

	@JsonProperty("id")
	private String id;

	@JsonProperty("label")
	private String label;

	public String getCode(){
		return code;
	}

	public boolean isIsDeleted(){
		return isDeleted;
	}

	public String getId(){
		return id;
	}

	public String getLabel(){
		return label;
	}
}