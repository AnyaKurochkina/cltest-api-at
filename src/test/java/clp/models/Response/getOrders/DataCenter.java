package clp.models.response.getOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataCenter{

	@JsonProperty("site")
	private String site;

	@JsonProperty("code")
	private String code;

	@JsonProperty("is_deleted")
	private boolean isDeleted;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("id")
	private String id;

	@JsonProperty("label")
	private String label;

	public String getSite(){
		return site;
	}

	public String getCode(){
		return code;
	}

	public boolean isIsDeleted(){
		return isDeleted;
	}

	public String getName(){
		return name;
	}

	public String getDescription(){
		return description;
	}

	public String getId(){
		return id;
	}

	public String getLabel(){
		return label;
	}
}