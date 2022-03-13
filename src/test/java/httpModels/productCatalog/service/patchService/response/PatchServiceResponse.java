package httpModels.productCatalog.service.patchService.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.service.copyService.response.DataSource;
import httpModels.productCatalog.service.copyService.response.ExtraData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PatchServiceResponse {

	@JsonProperty("allowed_paths")
	private List<Object> allowedPaths;

	@JsonProperty("is_published")
	private Boolean isPublished;

	@JsonProperty("icon")
	private Object icon;

	@JsonProperty("graph_version")
	private String graphVersion;

	@JsonProperty("description")
	private String description;

	@JsonProperty("restricted_groups")
	private List<Object> restrictedGroups;

	@JsonProperty("graph_id")
	private String graphId;

	@JsonProperty("data_source")
	private DataSource dataSource;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("direction_id")
	private String directionId;

	@JsonProperty("extra_data")
	private ExtraData extraData;

	@JsonProperty("name")
	private String name;

	@JsonProperty("restricted_paths")
	private List<Object> restrictedPaths;

	@JsonProperty("graph_version_pattern")
	private String graphVersionPattern;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("id")
	private String id;

	public void setAllowedPaths(List<Object> allowedPaths){
		this.allowedPaths = allowedPaths;
	}

	public List<Object> getAllowedPaths(){
		return allowedPaths;
	}

	public void setIsPublished(Boolean isPublished){
		this.isPublished = isPublished;
	}

	public Boolean isIsPublished(){
		return isPublished;
	}

	public void setIcon(Object icon){
		this.icon = icon;
	}

	public Object getIcon(){
		return icon;
	}

	public void setGraphVersion(String graphVersion){
		this.graphVersion = graphVersion;
	}

	public String getGraphVersion(){
		return graphVersion;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setRestrictedGroups(List<Object> restrictedGroups){
		this.restrictedGroups = restrictedGroups;
	}

	public List<Object> getRestrictedGroups(){
		return restrictedGroups;
	}

	public void setGraphId(String graphId){
		this.graphId = graphId;
	}

	public String getGraphId(){
		return graphId;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public DataSource getDataSource(){
		return dataSource;
	}

	public void setNumber(Integer number){
		this.number = number;
	}

	public Integer getNumber(){
		return number;
	}

	public void setDirectionId(String directionId){
		this.directionId = directionId;
	}

	public String getDirectionId(){
		return directionId;
	}

	public void setExtraData(ExtraData extraData){
		this.extraData = extraData;
	}

	public ExtraData getExtraData(){
		return extraData;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setRestrictedPaths(List<Object> restrictedPaths){
		this.restrictedPaths = restrictedPaths;
	}

	public List<Object> getRestrictedPaths(){
		return restrictedPaths;
	}

	public void setGraphVersionPattern(String graphVersionPattern){
		this.graphVersionPattern = graphVersionPattern;
	}

	public String getGraphVersionPattern(){
		return graphVersionPattern;
	}

	public void setAllowedGroups(List<Object> allowedGroups){
		this.allowedGroups = allowedGroups;
	}

	public List<Object> getAllowedGroups(){
		return allowedGroups;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}
}