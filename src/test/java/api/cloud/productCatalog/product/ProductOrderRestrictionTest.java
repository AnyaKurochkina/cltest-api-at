package api.cloud.productCatalog.product;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.product.ProductOrderRestriction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.orderService.OrderServiceSteps.getProductOrderService;
import static steps.productCatalog.ProductSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductOrderRestrictionTest extends Tests {

    @DisplayName("")
    @TmsLink("")
    @Test
    public void getProductOrderRestrictionTest() {
        Product product = createProductByName("get_product_order_restriction");
        JSONObject json = ProductOrderRestriction.builder().build().toJson();
        createProductOrderRestrictionById(getProductByName("ubuntu").getProductId(), json);
        Response response = getProductOrderRestrictionById("4d8806fa-951a-4268-a885-f1474519ce02");
        Response response2 = getProductOrderService(getProductById("4d8806fa-951a-4268-a885-f1474519ce02").getName());
        assertEquals(response, response2);
    }
}
