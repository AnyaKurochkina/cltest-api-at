package api.cloud.productCatalog.allowedAction;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.AllowedActionSteps.createAllowedAction;
import static steps.productCatalog.AllowedActionSteps.exportAllowedActionById;
import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Разрешенные действия")
@DisabledIfEnv("prod")
public class AllowedActionExportTest extends Tests {
    private static AllowedAction simpleAllowedAction;
    private static AllowedAction simpleAllowedAction2;

    @BeforeAll
    public static void setUp() {
        simpleAllowedAction = createAllowedAction("export_allowed_action1_test_api");
        simpleAllowedAction2 = createAllowedAction("export_allowed_action2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких разрешенных действий")
    @TmsLink("1507973")
    @Test
    public void exportAllowedActionsTest() {
        ExportEntity e = new ExportEntity(simpleAllowedAction.getId());
        ExportEntity e2 = new ExportEntity(simpleAllowedAction2.getId());
        Response response = exportObjectsById("allowed_actions", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт разрешенного действия по Id")
    @TmsLink("1507936")
    @Test
    public void exportAllowedActionByIdTest() {
        String allowedActionTitle = "allowed_action_export_test_api";
        AllowedAction allowedAction = createAllowedAction(allowedActionTitle);
        exportAllowedActionById(String.valueOf(allowedAction.getId()));
    }

    @DisplayName("Проверка поля ExportedObjects при экспорте разрешенного действия")
    @TmsLink("SOUL-7079")
    @Test
    public void checkExportedObjectsFieldAllowedAction() {
        AllowedAction allowedAction = createAllowedAction("allowed_action_exported_objects_test_api");
        Response response = exportAllowedActionById(String.valueOf(allowedAction.getId()));
        LinkedHashMap r = response.jsonPath().get("exported_objects.AllowedAction.");
        String result = r.keySet().stream().findFirst().get().toString();
        JSONObject jsonObject = new JSONObject(result);
        assertEquals(allowedAction.getName(), jsonObject.get("name").toString());
    }
}
