package api.cloud.productCatalog.action;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.action.Action;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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
}
