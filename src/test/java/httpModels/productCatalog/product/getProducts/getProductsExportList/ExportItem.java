package httpModels.productCatalog.product.getProducts.getProductsExportList;

import com.fasterxml.jackson.annotation.JsonProperty;
import httpModels.productCatalog.ItemImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportItem implements ItemImpl {

	@JsonProperty("restricted_developers")
	private List<Object> restrictedDevelopers;

	@JsonProperty("is_open")
	private Boolean isOpen;

	@JsonProperty("in_general_list")
	private Boolean inGeneralList;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("current_version")
	private String currentVersion;

	@JsonProperty("description")
	private String description;

	@JsonProperty("envs")
	private List<Object> envs;

	@JsonProperty("restricted_groups")
	private List<Object> restrictedGroups;

	@JsonProperty("org_info_systems")
	private LinkedHashMap<String, List<String>> orgInfoSystems;

	@JsonProperty("title")
	private String title;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("category_v2")
	private String categoryV2;

	@JsonProperty("allowed_developers")
	private List<Object> allowedDevelopers;

	@JsonProperty("update_dt")
	private String updateDt;

	@JsonProperty("name")
	private String name;

	@JsonProperty("create_dt")
	private String createDt;

	@JsonProperty("allowed_groups")
	private List<Object> allowedGroups;

	@JsonProperty("payment")
	private String payment;

	@JsonProperty("id")
	private String id;

	@JsonProperty("category")
	private String category;

	@JsonProperty("info")
	private LinkedHashMap<String, String> info;

	@Override
	public String getCreateData() {
		return null;
	}

	@Override
	public String getUpDateData() {
		return null;
	}
}