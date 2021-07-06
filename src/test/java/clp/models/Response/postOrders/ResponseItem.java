package clp.models.response.postOrders;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseItem{

	@JsonProperty("order_errors")
	private OrderErrors orderErrors;

	@JsonProperty("power_status")
	private List<Object> powerStatus;

	@JsonProperty("deletable")
	private boolean deletable;

	@JsonProperty("request_params")
	private RequestParams requestParams;

	@JsonProperty("created_at")
	private String createdAt;

	@JsonProperty("product_data")
	private ProductData productData;

	@JsonProperty("label")
	private String label;

	@JsonProperty("project_name")
	private String projectName;

	@JsonProperty("attrs")
	private Attrs attrs;

	@JsonProperty("domain_id")
	private Object domainId;

	@JsonProperty("data_center_id")
	private String dataCenterId;

	@JsonProperty("updated_at")
	private String updatedAt;

	@JsonProperty("product_label")
	private Object productLabel;

	@JsonProperty("product_id")
	private String productId;

	@JsonProperty("platform_id")
	private Object platformId;

	@JsonProperty("id")
	private String id;

	@JsonProperty("category")
	private String category;

	@JsonProperty("net_segment_id")
	private String netSegmentId;

	@JsonProperty("status")
	private String status;

	@JsonProperty("deploy_info")
	private DeployInfo deployInfo;

	public OrderErrors getOrderErrors(){
		return orderErrors;
	}

	public List<Object> getPowerStatus(){
		return powerStatus;
	}

	public boolean isDeletable(){
		return deletable;
	}

	public RequestParams getRequestParams(){
		return requestParams;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public ProductData getProductData(){
		return productData;
	}

	public String getLabel(){
		return label;
	}

	public String getProjectName(){
		return projectName;
	}

	public Attrs getAttrs(){
		return attrs;
	}

	public Object getDomainId(){
		return domainId;
	}

	public String getDataCenterId(){
		return dataCenterId;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public Object getProductLabel(){
		return productLabel;
	}

	public String getProductId(){
		return productId;
	}

	public Object getPlatformId(){
		return platformId;
	}

	public String getId(){
		return id;
	}

	public String getCategory(){
		return category;
	}

	public String getNetSegmentId(){
		return netSegmentId;
	}

	public String getStatus(){
		return status;
	}

	public DeployInfo getDeployInfo(){
		return deployInfo;
	}
}