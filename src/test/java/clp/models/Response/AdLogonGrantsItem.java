package clp.models.Response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AdLogonGrantsItem{

	@JsonProperty("role")
	private String role;

	@JsonProperty("groups")
	private List<String> groups;

	public void setRole(String role){
		this.role = role;
	}

	public String getRole(){
		return role;
	}

	public void setGroups(List<String> groups){
		this.groups = groups;
	}

	public List<String> getGroups(){
		return groups;
	}
}