package tests.productCatalog.forbiddenAction;

import core.helper.Configure;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.forbiddenAction.ForbiddenAction;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ForbiddenActionSteps.deleteForbiddenActionByName;
import static steps.productCatalog.ForbiddenActionSteps.isForbiddenActionExists;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionTest extends Tests {
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/forbidden_actions/",
            "productCatalog/graphs/createGraph.json");

    @DisplayName("Создание запрещенного действия действия")
    @TmsLink("")
    @Test
    public void createForbiddenAction() {
        ForbiddenAction forbiddenAction = ForbiddenAction.builder()
                .name("create_forbidden_action_test_api")
                .title("create_forbidden_action_test_api")
                .build()
                .createObject();
    }

    @DisplayName("Импорт запрещенного действия действия")
    @TmsLink("")
    @Test
    public void importForbiddenAction() {
        String data = JsonHelper.getStringFromFile("/productCatalog/forbiddenAction/importForbiddenAction.json");
        String forbiddenActionName = new JsonPath(data).get("ForbiddenAction.name");
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/forbiddenAction/importForbiddenAction.json");
        assertTrue(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие не существует");
        deleteForbiddenActionByName(forbiddenActionName);
        assertFalse(isForbiddenActionExists(forbiddenActionName), "Запрещенное действие существует");
    }
}
