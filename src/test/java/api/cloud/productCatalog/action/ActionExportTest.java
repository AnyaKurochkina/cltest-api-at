package api.cloud.productCatalog.action;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.action.Action;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.ActionSteps.exportActionById;
import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionExportTest extends Tests {
    private static Action simpleAction;
    private static Action simpleAction2;

    @BeforeAll
    public static void setUp() {
        simpleAction = createAction("export_action1_test_api");
        simpleAction2 = createAction("export_action2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких действий")
    @TmsLink("1531483")
    @Test
    public void exportActionsTest() {
        ExportEntity e = new ExportEntity(simpleAction.getActionId(), simpleAction.getVersion());
        ExportEntity e2 = new ExportEntity(simpleAction2.getActionId(), simpleAction2.getVersion());
        exportObjectsById("actions", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт действия по Id")
    @TmsLink("642499")
    @Test
    public void exportActionByIdTest() {
        String actionName = "action_export_test_api";
        Action action = createAction(actionName);
        exportActionById(action.getActionId());
    }

    @DisplayName("Проверка поля ExportedObjects при экспорте действия")
    @TmsLink("SOUL-7077")
    @Test
    public void checkExportedObjectsField() {
        String actionName = "action_exported_objects_test_api";
        Action action = createAction(actionName);
        Response response = exportActionById(action.getActionId());
        LinkedHashMap r = response.jsonPath().get("exported_objects.Action.");
        String result = r.keySet().stream().findFirst().get().toString();
        JSONObject jsonObject = new JSONObject(result);
        assertEquals(action.getLastVersion(), jsonObject.get("last_version_str").toString());
        assertEquals(action.getName(), jsonObject.get("name").toString());
        assertEquals(action.getVersion(), jsonObject.get("version").toString());
    }
}
