package clp.models.Response;

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

	public void setOrderErrors(OrderErrors orderErrors){
		this.orderErrors = orderErrors;
	}

	public OrderErrors getOrderErrors(){
		return orderErrors;
	}

	public void setPowerStatus(List<Object> powerStatus){
		this.powerStatus = powerStatus;
	}

	public List<Object> getPowerStatus(){
		return powerStatus;
	}

	public void setDeletable(boolean deletable){
		this.deletable = deletable;
	}

	public boolean isDeletable(){
		return deletable;
	}

	public void setRequestParams(RequestParams requestParams){
		this.requestParams = requestParams;
	}

	public RequestParams getRequestParams(){
		return requestParams;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setProductData(ProductData productData){
		this.productData = productData;
	}

	public ProductData getProductData(){
		return productData;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public String getLabel(){
		return label;
	}

	public void setProjectName(String projectName){
		this.projectName = projectName;
	}

	public String getProjectName(){
		return projectName;
	}

	public void setAttrs(Attrs attrs){
		this.attrs = attrs;
	}

	public Attrs getAttrs(){
		return attrs;
	}

	public void setDomainId(Object domainId){
		this.domainId = domainId;
	}

	public Object getDomainId(){
		return domainId;
	}

	public void setDataCenterId(String dataCenterId){
		this.dataCenterId = dataCenterId;
	}

	public String getDataCenterId(){
		return dataCenterId;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setProductLabel(Object productLabel){
		this.productLabel = productLabel;
	}

	public Object getProductLabel(){
		return productLabel;
	}

	public void setProductId(String productId){
		this.productId = productId;
	}

	public String getProductId(){
		return productId;
	}

	public void setPlatformId(Object platformId){
		this.platformId = platformId;
	}

	public Object getPlatformId(){
		return platformId;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setNetSegmentId(String netSegmentId){
		this.netSegmentId = netSegmentId;
	}

	public String getNetSegmentId(){
		return netSegmentId;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setDeployInfo(DeployInfo deployInfo){
		this.deployInfo = deployInfo;
	}

	public DeployInfo getDeployInfo(){
		return deployInfo;
	}
}