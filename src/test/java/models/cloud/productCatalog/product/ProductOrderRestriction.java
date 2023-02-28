package models.cloud.productCatalog.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.helper.JsonHelper;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

@Log4j2
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderRestriction {

    @JsonProperty("order_restriction")
    private OrderRestriction orderRestriction;

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate("productCatalog/products/createProductRestriction.json")
                .set("$.order_restriction", orderRestriction)
                .build();
    }
}