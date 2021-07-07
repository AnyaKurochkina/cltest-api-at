package clp.models.response.getOrders;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AdLogonGrantsItem{

	@JsonProperty("role")
	private String role;

	@JsonProperty("groups")
	private List<String> groups;

	public String getRole(){
		return role;
	}

	public List<String> getGroups(){
		return groups;
	}
}