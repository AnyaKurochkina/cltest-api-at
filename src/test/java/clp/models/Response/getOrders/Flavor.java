package clp.models.response.getOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Flavor{

	@JsonProperty("memory")
	private int memory;

	@JsonProperty("cpus")
	private int cpus;

	@JsonProperty("name")
	private String name;

	@JsonProperty("uuid")
	private String uuid;

	public int getMemory(){
		return memory;
	}

	public int getCpus(){
		return cpus;
	}

	public String getName(){
		return name;
	}

	public String getUuid(){
		return uuid;
	}
}