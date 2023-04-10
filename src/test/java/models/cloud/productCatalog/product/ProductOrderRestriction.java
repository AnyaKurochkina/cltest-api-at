package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.util.List;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@JsonRootName(value = "order_restriction")
@EqualsAndHashCode(exclude = "updatedAt")
@ToString
public class ProductOrderRestriction {

    private List<Object> environments;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("net_segments")
    private List<String> netSegments;
    private List<String> domains;
    @JsonProperty("information_system_ids")
    private List<String> informSystemIds;
    @JsonProperty("product_name")
    private String productName;
    private List<String> platforms;
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
    private Integer weight;
    private String id;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/products/createProductRestriction.json")
                .set("order_restriction.product_name", productName)
                .set("order_restriction.weight", weight)
                .set("order_restriction.platforms", platforms)
                .set("order_restriction.environments", environments)
                .build();
    }
}