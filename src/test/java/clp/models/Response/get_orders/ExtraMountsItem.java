package clp.models.response.get_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtraMountsItem{

	@JsonProperty("path")
	private String path;

	@JsonProperty("file_system")
	private String fileSystem;

	@JsonProperty("size")
	private int size;

	public String getPath(){
		return path;
	}

	public String getFileSystem(){
		return fileSystem;
	}

	public int getSize(){
		return size;
	}
}