package clp.models.response.getOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductData{

	@JsonProperty("hostname")
	private String hostname;

	@JsonProperty("ip")
	private String ip;

	public String getHostname(){
		return hostname;
	}

	public String getIp(){
		return ip;
	}
}