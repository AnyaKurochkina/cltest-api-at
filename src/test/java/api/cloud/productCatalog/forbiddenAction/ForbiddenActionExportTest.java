package api.cloud.productCatalog.forbiddenAction;

import api.Tests;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ForbiddenActionSteps.createForbiddenAction;
import static steps.productCatalog.ForbiddenActionSteps.exportForbiddenActionById;
import static steps.productCatalog.ProductCatalogSteps.exportObjectsById;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Запрещенные действия")
@DisabledIfEnv("prod")
public class ForbiddenActionExportTest extends Tests {
    private static ForbiddenAction simpleForbiddenAction;
    private static ForbiddenAction simpleForbiddenAction2;

    @BeforeAll
    public static void setUp() {
        simpleForbiddenAction = createForbiddenAction("export_forbidden_action1_test_api");
        simpleForbiddenAction2 = createForbiddenAction("export_forbidden_action2_test_api");
    }

    @SneakyThrows
    @DisplayName("Экспорт нескольких запрещенных действий")
    @TmsLink("1518530")
    @Test
    public void exportForbiddenActionsTest() {
        ExportEntity e = new ExportEntity(simpleForbiddenAction.getId());
        ExportEntity e2 = new ExportEntity(simpleForbiddenAction2.getId());
        exportObjectsById("forbidden_actions", new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт запрещенного действия по Id")
    @TmsLink("1518531")
    @Test
    public void exportForbiddenActionByIdTest() {
        ForbiddenAction forbiddenAction = createForbiddenAction("forbidden_action_export_test_api");
        exportForbiddenActionById(String.valueOf(forbiddenAction.getId()));
    }

    @DisplayName("Проверка поля ExportedObjects при экспорте запрещенного действия")
    @TmsLink("SOUL-")
    @Test
    public void checkExportedObjectsFieldForbiddenAction() {
        ForbiddenAction forbiddenAction = createForbiddenAction("forbidden_action_exported_objects_test_api");
        Response response = exportForbiddenActionById(String.valueOf(forbiddenAction.getId()));
        LinkedHashMap r = response.jsonPath().get("exported_objects.ForbiddenAction.");
        String result = r.keySet().stream().findFirst().get().toString();
        JSONObject jsonObject = new JSONObject(result);
        assertEquals(forbiddenAction.getName(), jsonObject.get("name").toString());
    }
}
