package httpModels.productCatalog.graphs.getGraph.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphItem{
	@JsonProperty("is_sequential")
	private Boolean isSequential;

	@JsonProperty("hold")
	private Boolean hold;

	@JsonProperty("template_version")
	private String templateVersion;

	@JsonProperty("color")
	private String color;

	@JsonProperty("subgraph_version")
	private String subgraphVersion;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("icon_url")
	private String iconUrl;

	@JsonProperty("subgraph_version_pattern")
	private String subgraphVersionPattern;

	@JsonProperty("for_each")
	private Object forEach;

	@JsonProperty("description")
	private String description;

	@JsonProperty("timeout")
	private Integer timeout;

	@JsonProperty("output")
	private Map<String, Object> output;

	@JsonProperty("coords_x")
	private Integer coordsX;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("template_version_pattern")
	private String templateVersionPattern;

	@JsonProperty("printed_output")
	private Object/*<PrintedOutput>*/ printedOutput;

	@JsonProperty("coords_y")
	private Integer coordsY;

	@JsonProperty("depends")
	private List<Object> depends;

	@JsonProperty("count")
	private Object count;

	@JsonProperty("log_level")
	private Object logLevel;

	@JsonProperty("damage_order_on_error")
	private Boolean damageOrderOnError;

	@JsonProperty("priority")
	private Object priority;

	@JsonProperty("subgraph_id")
	private Object subgraphId;

	@JsonProperty("on_prebilling")
	private Boolean onPrebilling;

	@JsonProperty("input")
	private Map<String, String> input;

	@JsonProperty("not_damage_on_error")
	private Boolean notDamageOnError;

	@JsonProperty("condition")
	private String condition;

	@JsonProperty("extra_data")
	private Object extraData;

	@JsonProperty("name")
	private String name;

	@JsonProperty("template_id")
	private String templateId;

	@JsonProperty("run_on_rollback")
	private Boolean runOnRollback;

	@JsonProperty("template_version_calculated")
	private Object templateVersionCalculated;

	@JsonProperty("subgraph_version_calculated")
	private Object subgraphVersionCalculated;

	@JsonProperty("lock_order_on_error")
	private Boolean lockOrderOnError;

	@JsonProperty("icon_store_id")
	private Object iconStoreId;
}