package clp.models.response.postOrders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtraDisksItem{

	@JsonProperty("size")
	private int size;

	public int getSize(){
		return size;
	}
}