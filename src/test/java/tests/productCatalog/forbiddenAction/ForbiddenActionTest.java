package tests.productCatalog.forbiddenAction;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionTest extends Tests {
    @DisplayName("Импорт запрещенного действия действия")
    @TmsLink("")
    @Test
    public void importAction() {
//        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction.json");
//        String actionName = new JsonPath(data).get("Action.name");
//        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importAction.json");
//        assertTrue(steps.isExists(actionName), "Действие не существует");
//        steps.deleteByName(actionName, GetActionsListResponse.class);
//        assertFalse(steps.isExists(actionName), "Действие существует");
    }
}
