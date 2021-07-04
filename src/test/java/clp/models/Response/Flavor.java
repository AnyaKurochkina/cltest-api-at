package clp.models.Response;

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

	public void setMemory(int memory){
		this.memory = memory;
	}

	public int getMemory(){
		return memory;
	}

	public void setCpus(int cpus){
		this.cpus = cpus;
	}

	public int getCpus(){
		return cpus;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}
}