package clp.models.response.postOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BootDisk{

	@JsonProperty("size")
	private int size;

	public int getSize(){
		return size;
	}
}