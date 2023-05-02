package models.cloud.productCatalog.template;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodesItem{

	@JsonProperty("template_version_calculated")
	private String templateVersionCalculated;

	@JsonProperty("template_version")
	private String templateVersion;

	@JsonProperty("template_version_pattern")
	private String templateVersionPattern;

	@JsonProperty("node_name")
	private String nodeName;
}