package clp.models.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtraDisksItem{

	@JsonProperty("size")
	private int size;

	public void setSize(int size){
		this.size = size;
	}

	public int getSize(){
		return size;
	}
}