package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration{

	@JsonProperty("max_connections")
	private int maxConnections;

	public int getMaxConnections(){
		return maxConnections;
	}
}