package clp.models.response.getOrders;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Response{

	@JsonProperty("data")
	private List<DataItem> data;

	@JsonProperty("net_segment")
	private NetSegment netSegment;

	@JsonProperty("power_status")
	private List<PowerStatusItem> powerStatus;

	@JsonProperty("deletable")
	private boolean deletable;

	@JsonProperty("created_at")
	private String createdAt;

	@JsonProperty("data_center")
	private DataCenter dataCenter;

	@JsonProperty("label")
	private String label;

	@JsonProperty("product_data")
	private ProductData productData;

	@JsonProperty("project_name")
	private String projectName;

	@JsonProperty("attrs")
	private Attrs attrs;

	@JsonProperty("updated_at")
	private String updatedAt;

	@JsonProperty("product_id")
	private String productId;

	@JsonProperty("id")
	private String id;

	@JsonProperty("category")
	private String category;

	@JsonProperty("status")
	private String status;

	public List<DataItem> getData(){
		return data;
	}

	public NetSegment getNetSegment(){
		return netSegment;
	}

	public List<PowerStatusItem> getPowerStatus(){
		return powerStatus;
	}

	public boolean isDeletable(){
		return deletable;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public DataCenter getDataCenter(){
		return dataCenter;
	}

	public String getLabel(){
		return label;
	}

	public ProductData getProductData(){
		return productData;
	}

	public String getProjectName(){
		return projectName;
	}

	public Attrs getAttrs(){
		return attrs;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public String getProductId(){
		return productId;
	}

	public String getId(){
		return id;
	}

	public String getCategory(){
		return category;
	}

	public String getStatus(){
		return status;
	}
}