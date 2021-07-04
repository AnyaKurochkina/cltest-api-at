package clp.models.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestParams{

	@JsonProperty("action_id")
	private String actionId;

	@JsonProperty("disable_rollback")
	private boolean disableRollback;

	@JsonProperty("params")
	private Params params;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("version")
	private String version;

	public void setActionId(String actionId){
		this.actionId = actionId;
	}

	public String getActionId(){
		return actionId;
	}

	public void setDisableRollback(boolean disableRollback){
		this.disableRollback = disableRollback;
	}

	public boolean isDisableRollback(){
		return disableRollback;
	}

	public void setParams(Params params){
		this.params = params;
	}

	public Params getParams(){
		return params;
	}

	public void setGraphId(String graphId){
		this.graphId = graphId;
	}

	public String getGraphId(){
		return graphId;
	}

	public void setVersion(String version){
		this.version = version;
	}

	public String getVersion(){
		return version;
	}
}