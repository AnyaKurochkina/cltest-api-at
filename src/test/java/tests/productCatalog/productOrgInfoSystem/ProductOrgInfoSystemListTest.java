package tests.productCatalog.productOrgInfoSystem;

import core.helper.Configure;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList.GetInfoSystemListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("ProductOrgInfoSystem")
@DisabledIfEnv("prod")
public class ProductOrgInfoSystemListTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/product_org_info_system/",
            "productCatalog/productOrgInfoSystem/createInfoSystem.json", Configure.ProductCatalogURL);

    @DisplayName("Получение списка информационных систем")
    @TmsLink("822012")
    @Test
    public void getProductInfoSystemList() {
        List<ItemImpl> list = steps.getProductObjectList(GetInfoSystemListResponse.class);
        assertTrue(list.size() > 0, "Список не отсортирован.");
    }
}
