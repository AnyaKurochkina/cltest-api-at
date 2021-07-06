package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Creator{

	@JsonProperty("firstname")
	private String firstname;

	@JsonProperty("id")
	private String id;

	@JsonProperty("email")
	private String email;

	@JsonProperty("lastname")
	private String lastname;

	@JsonProperty("username")
	private String username;

	public String getFirstname(){
		return firstname;
	}

	public String getId(){
		return id;
	}

	public String getEmail(){
		return email;
	}

	public String getLastname(){
		return lastname;
	}

	public String getUsername(){
		return username;
	}
}