package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRestriction{

	private List<Object> environments;
	@JsonProperty("created_at")
	private String createdAt;
	@JsonProperty("net_segments")
	private List<String> netSegments;
	private List<String> domains;
	@JsonProperty("product_name")
	private String productName;
	private List<Object> platforms;
	@JsonProperty("data_centers")
	private List<String> dataCenters;
	@JsonProperty("is_deleted")
	private Boolean isDeleted;
	@JsonProperty("updated_at")
	private String updatedAt;
	@JsonProperty("product_id")
	private String productId;
	private String organization;
	@JsonProperty("is_blocking")
	private Boolean isBlocking;
	private String id;
}