package api.cloud.productCatalog.action;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.ExportData;
import models.cloud.productCatalog.ExportEntity;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;
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

    @DisplayName("Экспорт действия по Id с tag_list")
    @TmsLink("")
    @Test
    public void exportActionByIdWithTagListTest() {
        String actionName = "action_export_with_tag_list_test_api";
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        Graph graph = createGraph("graph_for_export_with_tags_test");
        List<String> expectedTagList = Arrays.asList("export_test", "test2");
        JSONObject jsonObject = Action.builder()
                .name(actionName)
                .graphId(graph.getGraphId())
                .tagList(expectedTagList)
                .build()
                .toJson();
        Action action = createAction(jsonObject).extractAs(Action.class);
        List<String> actualTagList = exportActionByIdWithTags(action.getActionId()).jsonPath().getList("Action.tag_name_list");
        assertEquals(actualTagList, expectedTagList);
        deleteActionById(action.getActionId());
    }
}
