package api.cloud.productCatalog.action;

import core.helper.http.Response;
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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ProductCatalogSteps.exportObjectByIdWithTags;

@Epic("Продуктовый каталог")
@Feature("Действия")
@DisabledIfEnv("prod")
public class ActionExportTest extends ActionBaseTest {
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
        ExportEntity e = new ExportEntity(simpleAction.getId(), simpleAction.getVersion());
        ExportEntity e2 = new ExportEntity(simpleAction2.getId(), simpleAction2.getVersion());
        exportActions(new ExportData(Arrays.asList(e, e2)).toJson());
    }

    @DisplayName("Экспорт действия по Id")
    @TmsLink("642499")
    @Test
    public void exportActionByIdTest() {
        String actionName = "action_export_test_api";
        Action action = createAction(actionName);
        exportActionById(action.getId());
    }

    @DisplayName("Проверка поля ExportedObjects при экспорте действия")
    @TmsLink("SOUL-7077")
    @Test
    public void checkExportedObjectsField() {
        String actionName = "action_exported_objects_test_api";
        Action action = createAction(actionName);
        Response response = exportActionById(action.getId());
        LinkedHashMap r = response.jsonPath().get("exported_objects.Action.");
        String result = r.keySet().stream().findFirst().get().toString();
        JSONObject jsonObject = new JSONObject(result);
        assertEquals(action.getLastVersion(), jsonObject.get("last_version_str").toString());
        assertEquals(action.getName(), jsonObject.get("name").toString());
        assertEquals(action.getVersion(), jsonObject.get("version").toString());
    }

    @DisplayName("Экспорт действия по Id с tag_list")
    @TmsLink("SOUL-7101")
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
        List<String> actualTagList = exportObjectByIdWithTags("actions", action.getId()).jsonPath().getList("Action.tag_name_list");
        assertEquals(actualTagList, expectedTagList);
        deleteActionById(action.getId());
    }

    @DisplayName("Проверка current_version при экспорте действия")
    @TmsLink("SOUL-7757")
    @Test
    public void checkCurrentVersionActionExportTest() {
        String actionName = "check_current_version_action_export_test_api";
        Action action = createAction(actionName);
        partialUpdateAction(action.getId(), new JSONObject()
                .put("priority", 4)
                .put("version", "1.1.1"));
        partialUpdateAction(action.getId(), new JSONObject()
                .put("current_version", "1.1.1"));
        String currentVersion = exportActionById(action.getId()).jsonPath().getString("Action.current_version");
        assertNull(currentVersion);
    }
}
