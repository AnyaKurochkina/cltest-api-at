package steps.orderService;

import core.enums.Role;
import lombok.Builder;
import lombok.Data;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.orderService.interfaces.ProductStatus;
import org.json.JSONObject;

import java.time.Duration;

@Data
@Builder
public class ActionParameters {
    private String name;
    private String itemId;
    private Boolean skipOnPrebilling;
    private String orderId;
    private JSONObject data;
    private String projectId;
    @Builder.Default
    private String filter = "";
    @Builder.Default
    private Duration timeout = Duration.ofMinutes(25);
    private Role role;

    private ProductStatus status;
    private IProduct product;

    public static class ActionParametersBuilder {
        public ActionParametersBuilder product(IProduct product) {
            this.projectId = product.getProjectId();
            this.orderId = product.getOrderId();
            return this;
        }
    }
}
