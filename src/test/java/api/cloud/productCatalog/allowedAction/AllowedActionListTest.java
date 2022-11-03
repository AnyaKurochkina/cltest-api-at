package api.cloud.productCatalog.allowedAction;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import api.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.AllowedActionSteps.getAllowedActionList;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Разрешенные Действия")
@DisabledIfEnv("prod")
public class AllowedActionListTest extends Tests {

    @DisplayName("Получение списка разрешенных действий")
    @TmsLink("1242975")
    @Test
    public void getAllowedActionListTest() {
        String actionName = "get_allowed_action_list_test_api";
        AllowedAction action = AllowedAction.builder()
                .name(actionName)
                .title(actionName)
                .build()
                .createObject();
        List<AllowedAction> allowedActionList = getAllowedActionList();
        assertTrue(allowedActionList.contains(action));
    }
}
