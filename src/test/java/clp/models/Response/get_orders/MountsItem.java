package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MountsItem{

	@JsonProperty("size")
	private double size;

	@JsonProperty("options")
	private String options;

	@JsonProperty("mount")
	private String mount;

	@JsonProperty("device")
	private String device;

	@JsonProperty("fstype")
	private String fstype;

	public double getSize(){
		return size;
	}

	public String getOptions(){
		return options;
	}

	public String getMount(){
		return mount;
	}

	public String getDevice(){
		return device;
	}

	public String getFstype(){
		return fstype;
	}
}