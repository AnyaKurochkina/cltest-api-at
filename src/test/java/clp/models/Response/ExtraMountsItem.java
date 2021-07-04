package clp.models.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtraMountsItem{

	@JsonProperty("path")
	private String path;

	@JsonProperty("file_system")
	private String fileSystem;

	@JsonProperty("size")
	private int size;

	public void setPath(String path){
		this.path = path;
	}

	public String getPath(){
		return path;
	}

	public void setFileSystem(String fileSystem){
		this.fileSystem = fileSystem;
	}

	public String getFileSystem(){
		return fileSystem;
	}

	public void setSize(int size){
		this.size = size;
	}

	public int getSize(){
		return size;
	}
}