package api.cloud.productCatalog.action;

import core.helper.Configure;
import core.helper.DataFileHelper;
import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.RESOURCE_PATH;
import static core.utils.AssertUtils.assertEqualsList;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.GraphSteps.deleteGraphById;
import static steps.productCatalog.ProductSteps.importProduct;

@Epic("Продуктовый каталог")
@Feature("Действия")
public class ActionImportTest extends ActionBaseTest {

    @DisplayName("Импорт действия")
    @TmsLink("642433")
    @Test
    public void importActionTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction.json");
        String actionName = new JsonPath(data).get("Action.name");
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        ImportObject importObject = importAction(RESOURCE_PATH + "/json/productCatalog/actions/importAction.json");
        assertEquals(actionName, importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isActionExists(actionName), "Действие не существует");
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName), "Действие существует");
    }

    @DisplayName("Импорт действия с tag_list")
    @TmsLink("SOUL-7102")
    @Test
    public void importActionWithTagListTest() {
        String actionName = "action_import_with_tag_list_test_api";
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/actions/importActionWithTags.json";
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        Graph graph = createGraph("graph_action_import_for_export_with_tags_test");
        List<String> expectedTagList = Arrays.asList("import_test", "test_import");
        Action actionModel = createActionModel(actionName);
        actionModel.setTagList(expectedTagList);
        actionModel.setGraphId(graph.getGraphId());
        Action action = createAction(actionModel.toJson()).extractAs(Action.class);

        DataFileHelper.write(filePath, exportObjectByIdWithTags("actions", action.getId()).toString());
        deleteActionByName(actionName);
        importActionWithTagList(filePath);
        assertEquals(expectedTagList, getActionByName(actionName).getTagList());
        deleteActionByName(actionName);
    }

    @DisplayName("Добавление новых tags при импорте действия")
    @TmsLink("SOUL-7120")
    @Test
    public void checkNewTagsAddedWhenImportTest() {
        String actionName = "action_import_with_new_tag_list_test_api";
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/actions/importActionWithNewTags.json";
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        Graph graph = createGraph("graph_action_import_for_export_with_new_tags_test");
        List<String> addTagList = Collections.singletonList("new_tag");
        JSONObject jsonObject = Action.builder()
                .name(actionName)
                .graphId(graph.getGraphId())
                .tagList(Arrays.asList("import_test", "test_import"))
                .build()
                .toJson();
        Action action = createAction(jsonObject).extractAs(Action.class);
        DataFileHelper.write(filePath, exportObjectByIdWithTags("actions", action.getId()).toString());
        String updatedJsonForImport = JsonHelper.getJsonTemplate("/productCatalog/actions/importActionWithNewTags.json")
                .set("Action.tag_name_list", addTagList)
                .set("Action.version_arr", Arrays.asList(1, 0, 1))
                .build()
                .toString();
        DataFileHelper.write(filePath, updatedJsonForImport);

        importObjectWithTagList("actions", filePath);
        List<String> expectedTags = Arrays.asList("new_tag", "import_test", "test_import");
        List<String> actualTags = getActionByName(actionName).getTagList();
        assertEqualsList(expectedTags, actualTags);
        deleteActionByName(actionName);
    }

    @DisplayName("Импорт нескольких действий")
    @TmsLink("1531556")
    @Test
    public void importActionsTest() {
        String actionName = "multi_import_action_test_api";
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        String actionName2 = "multi_import_action2_test_api";
        if (isActionExists(actionName2)) {
            deleteActionByName(actionName2);
        }
        String graphId = createGraph(RandomStringUtils.randomAlphabetic(10).toLowerCase()).getGraphId();
        Action actionModel = createActionModel(actionName);
        actionModel.setGraphId(graphId);
        Action actionModel2 = createActionModel(actionName2);
        actionModel2.setGraphId(graphId);
        Action action = createAction(actionModel);
        Action action2 = createAction(actionModel2);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/actions/multiImportAction.json";
        String filePath2 = Configure.RESOURCE_PATH + "/json/productCatalog/actions/multiImportAction2.json";
        DataFileHelper.write(filePath, exportActionById(action.getId()).toString());
        DataFileHelper.write(filePath2, exportActionById(action2.getId()).toString());
        deleteActionByName(actionName);
        deleteActionByName(actionName2);
        importObjects("actions", filePath, filePath2);
        DataFileHelper.delete(filePath);
        DataFileHelper.delete(filePath2);
        assertTrue(isActionExists(actionName), "Действие не существует");
        assertTrue(isActionExists(actionName2), "Действие не существует");
        deleteActionByName(actionName);
        deleteActionByName(actionName2);
    }

    @DisplayName("Импорт уже существующего действия")
    @TmsLink("1319922")
    @Test
    public void importExistActionTest() {
        Action action = createAction(createActionModel("import_exist_action_test_api"));
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/actions/existActionImport.json";
        DataFileHelper.write(filePath, exportActionById(action.getId()).toString());
        ImportObject importObject = importAction(filePath);
        DataFileHelper.delete(filePath);
        assertEquals("error", importObject.getStatus());
        assertEquals(String.format("Error loading dump: Версия \"%s\" %s:%s уже существует, но с другим наполнением. Измените значение версии (\"version_arr: [1, 0, 0]\") у импортируемого объекта и попробуйте снова.",
                        action.getVersion(), importObject.getModelName(), action.getName()),
                importObject.getMessages().get(0));
    }

    @DisplayName("Импорт действия c иконкой")
    @TmsLink("1085391")
    @Test
    public void importActionWithIcon() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importActionWithIcon.json");
        String actionName = new JsonPath(data).get("Action.name");
        String graphId = "";
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        try {
            importAction(Configure.RESOURCE_PATH + "/json/productCatalog/actions/importActionWithIcon.json");
            String id = getActionByName(actionName).getId();
            Action action = getActionById(id);
            graphId = action.getGraphId();
            assertFalse(action.getIconStoreId().isEmpty());
            assertFalse(action.getIconUrl().isEmpty());
            assertTrue(isActionExists(actionName), "Действие не существует");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteActionByName(actionName);
            deleteGraphById(graphId);
            assertFalse(isActionExists(actionName), "Действие существует");
        }
    }

    @Test
    @DisplayName("Негативный тест импорт действия в другой раздел")
    @TmsLink("1319697")
    public void importActionToAnotherSection() {
        String data = JsonHelper.getStringFromFile("/productCatalog/actions/importAction2.json");
        String actionName = new JsonPath(data).get("Action.name");
        if (isActionExists(actionName)) {
            deleteActionByName(actionName);
        }
        importProduct(RESOURCE_PATH + "/json/productCatalog/actions/importAction2.json");
        assertTrue(isActionExists(actionName), "Действие не существует");
        deleteActionByName(actionName);
        assertFalse(isActionExists(actionName), "Действие существует");
    }

    @DisplayName("Проверка не обновления неверсионных полей при импорте уже существующего действия")
    @TmsLink("SOUL-7454")
    @Test
    public void checkNotVersionedFieldsWhenImportedExistActionTest() {
        String description = "update description";
        String actionName = "check_not_versioned_fields__when_import_exist_action_test_api";
        Action action = createAction(actionName);
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/actions/checkNotVersionedFieldsExistActionImport.json";
        DataFileHelper.write(filePath, exportActionById(action.getId()).toString());
        partialUpdateAction(action.getId(), new JSONObject().put("description", description));
        importAction(filePath);
        DataFileHelper.delete(filePath);
        Action actionById = getActionById(action.getId());
        assertEquals(description, actionById.getDescription());
    }

    @DisplayName("Проверка current_version при импорте уже существующего действия")
    @TmsLink("SOUL-7756")
    @Test
    public void checkCurrentVersionWhenAlreadyExistActionImportTest() {
        String filePath = Configure.RESOURCE_PATH + "/json/productCatalog/actions/checkCurrentVersion.json";
        Action action = createAction(Action.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase())
                .version("1.0.1")
                .build().toJson()).extractAs(Action.class);
        DataFileHelper.write(filePath, exportActionById(action.getId()).toString());
        action.delete();
        Action createdAction = createAction(action.getName());
        partialUpdateAction(createdAction.getId(), new JSONObject()
                .put("priority", 4)
                .put("version", "1.1.1"));
        partialUpdateAction(createdAction.getId(), new JSONObject()
                .put("current_version", "1.1.1"));
        importAction(filePath);
        DataFileHelper.delete(filePath);
        Action actionById = getActionById(createdAction.getId());
        assertEquals("1.1.1", actionById.getCurrentVersion());
    }
}

