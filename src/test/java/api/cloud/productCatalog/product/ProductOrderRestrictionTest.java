package api.cloud.productCatalog.product;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.product.ProductOrderRestriction;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
        Product product = createProductByName("get_product_order_restriction_test_api");
        ProductOrderRestriction orderRestriction = ProductOrderRestriction.builder()
                .productName(product.getName())
                .weight(89)
                .platforms(Arrays.asList("vsphere"))
                .build();
    try {
        createProductOrderRestrictionById(product.getProductId(), orderRestriction.toJson());
    } catch (Exception e) {
        int a = 1;
        System.out.println("s");
    } finally {
        int f = 7;
    }
        Response response = getProductOrderRestrictionById("4d8806fa-951a-4268-a885-f1474519ce02");
//        Response response2 = getProductOrderService(getProductById("4d8806fa-951a-4268-a885-f1474519ce02").getName());
//        assertEquals(response, response2);
    }
}
