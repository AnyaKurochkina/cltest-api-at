package clp.models.response.postOrders;

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

	public String getActionId(){
		return actionId;
	}

	public boolean isDisableRollback(){
		return disableRollback;
	}

	public Params getParams(){
		return params;
	}

	public String getGraphId(){
		return graphId;
	}

	public String getVersion(){
		return version;
	}
}