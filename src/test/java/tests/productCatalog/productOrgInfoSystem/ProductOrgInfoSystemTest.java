package tests.productCatalog.productOrgInfoSystem;

import core.helper.Configure;
import core.helper.JsonHelper;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.productOrgInfoSystem.createInfoSystem.CreateInfoSystemResponse;
import httpModels.productCatalog.productOrgInfoSystem.getInfoSystemList.GetInfoSystemListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.authorizer.InformationSystem;
import models.productCatalog.Product;
import models.productCatalog.ProductOrgInfoSystem;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("ProductOrgInfoSystem")
@DisabledIfEnv("prod")
public class ProductOrgInfoSystemTest extends Tests {

    String orgName = "vtb";

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/product_org_info_system/",
            "productCatalog/productOrgInfoSystem/createInfoSystem.json");

    @Test
    @DisplayName("Создание productOrgInfoSystem")
    @TmsLink("822022")
    public void createInfoSystem() {
        String infoSysId = ((InformationSystem) InformationSystem.builder().build().createObject()).getId();
        Product product = Product.builder()
                .name("product_for_create_info_system_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .build()
                .createObject();
        ProductOrgInfoSystem productOrgInfoSystem = ProductOrgInfoSystem.builder()
                .organization(orgName)
                .product(product.getProductId())
                .informationSystems(Collections.singletonList(infoSysId))
                .build()
                .createObject();
        CreateInfoSystemResponse createdInfoSystem = steps.getProductOrgInfoSystem(product.getProductId(), orgName);
        assertEquals(createdInfoSystem.getId(), productOrgInfoSystem.getId());
    }

    @Test
    @DisplayName("Удаление productOrgInfoSystem")
    @TmsLink("822026")
    public void deleteInfoSystem() {
        String infoSysId = ((InformationSystem) InformationSystem.builder().build().createObject()).getId();
        Product product = Product.builder()
                .name("product_for_delete_info_system_test_api")
                .title("AtTestApiProduct")
                .envs(Collections.singletonList("dev"))
                .version("1.0.0")
                .build()
                .createObject();
        ProductOrgInfoSystem productOrgInfoSystem = ProductOrgInfoSystem.builder()
                .organization(orgName)
                .product(product.getProductId())
                .informationSystems(Collections.singletonList(infoSysId))
                .build()
                .createObject();
        steps.deleteProductOrgInfoSystem(product.getProductId(), orgName);
        List<ItemImpl> list = steps.getProductObjectList(GetInfoSystemListResponse.class);
        boolean result = false;
        for (ItemImpl item : list) {
           if(item.getId().equals(productOrgInfoSystem.getId())) {
               result = true;
               break;
           }
        }
        assertFalse(result);
    }

    @DisplayName("Импорт productOrgInfoSystem")
    @Disabled
    @TmsLink("")
    @Test
    public void importProductOrgInfoSystem() {
        String data = JsonHelper.getStringFromFile("/productCatalog/productOrgInfoSystem/importOrgInfoSystem.json");
        String actionName = new JsonPath(data).get("OrganizationInfoSystems.rel_foreign_models.product.Product.name");
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/productOrgInfoSystem/importOrgInfoSystem.json");
     //   steps.deleteByName(actionName, GetActionsListResponse.class);
    }
}
