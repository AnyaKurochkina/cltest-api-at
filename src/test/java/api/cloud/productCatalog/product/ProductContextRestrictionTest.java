package api.cloud.productCatalog.product;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.productCatalog.ContextRestrictionsItem;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.ProjectEnvironment;
import models.cloud.productCatalog.product.Product;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ProductSteps.getProductByProjectContext;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продукты")
@DisabledIfEnv("prod")
public class ProductContextRestrictionTest extends Tests {
    Project projectTest = Project.builder().isForOrders(true)
            .projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("TEST")).build().createObject();

    @DisplayName("Получение продукта с контекстным ограничением")
    @TmsLink("1533717")
    @Test
    public void getProductWithContextRestrictionTest() {
        ContextRestrictionsItem contItem = new ContextRestrictionsItem(new ProjectEnvironment(Collections.singletonList("dev")));
        String productName = "get_product_with_context_restriction_test_api";
        Product product = Product.builder()
                .name(productName)
                .title(productName)
                .contextRestrictions((Collections.singletonList(contItem)))
                .build()
                .createObject();
        String errorMsg = getProductByProjectContext(projectTest.getId(), product.getProductId()).assertStatus(404)
                .extractAs(ErrorMessage.class)
                .getMessage();
        assertEquals("No Product matches the given query.", errorMsg);
    }
}
