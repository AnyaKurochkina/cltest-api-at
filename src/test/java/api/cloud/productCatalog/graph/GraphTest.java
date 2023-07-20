package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.StringUtils;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Env;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.Modification;
import models.cloud.productCatalog.graph.RootPath;
import models.cloud.productCatalog.graph.UpdateType;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.service.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.*;

@Tag("product_catalog")
@Tag("Graphs")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphTest extends Tests {

    @DisplayName("Создание графа")
    @TmsLink("642536")
    @Test
    public void createGraphTest() {
        Graph graph = createGraph("create_graph_test_api");
        Graph createdGraph = getGraphById(graph.getGraphId());
        assertEquals(graph, createdGraph);
    }

    @DisplayName("Проверка существования графа по имени")
    @TmsLink("642540")
    @Test
    public void checkGraphExists() {
        Graph graph = createGraph("graph_check_exist_test_api");
        Assertions.assertTrue(isGraphExists(graph.getName()));
        Assertions.assertFalse(isGraphExists("NoExistsAction"));
    }

    @DisplayName("Получение графа по Id")
    @TmsLink("642631")
    @Test
    public void getGraphByIdTest() {
        Graph graph = createGraph("graph_get_by_id_test_api");
        Graph getGraph = getGraphById(graph.getGraphId());
        assertEquals(graph.getName(), getGraph.getName());
    }

    @DisplayName("Получение графа по Id и с параметром with_version_fields=true")
    @TmsLink("1284435")
    @Test
    public void getGraphByIdAndVersionFieldsTest() {
        Graph graph = createGraph("graph_get_by_id_and_version_fields_test_api");
        Graph getGraph = getGraphByIdAndFilter(graph.getGraphId(), "with_version_fields=true");
        assertFalse(getGraph.getVersionFields().isEmpty());
    }

    @DisplayName("Получение значения поля lock_order_on_error и проверка версии при изменении")
    @TmsLink("1022491")
    @Test
    public void getLockOrderOnErrorAndUpdate() {
        Graph graph = Graph.builder()
                .name("get_lock_order_on_error")
                .lockOrderOnError(false)
                .build()
                .createObject();
        String id = graph.getGraphId();
        Graph getGraph = getGraphById(id);
        assertFalse(getGraph.getLockOrderOnError());
        partialUpdateGraph(id, new JSONObject().put("lock_order_on_error", true));
        Graph getUpdatedGraph = getGraphById(id);
        assertTrue(getUpdatedGraph.getLockOrderOnError());
        String newVersion = getUpdatedGraph.getVersion();
        assertEquals("1.0.1", newVersion);
    }

    @DisplayName("Проверка сортировки по дате создания в графах")
    @TmsLink("740080")
    @Test
    public void orderingByCreateData() {
        List<Graph> list = getGraphListOrdering("create_dt");
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateDt());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в графах")
    @TmsLink("740082")
    @Test
    public void orderingByUpDateData() {
        List<Graph> list = getGraphListOrdering("update_dt");
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpdateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpdateDt());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в графах")
    @TmsLink("740085")
    @Test
    public void checkAccessWithPublicToken() {
        Graph graph = Graph.builder()
                .name("graph_get_by_name_with_public_token_test_api")
                .build()
                .createObject();
        getGraphByNameWithPublicToken(graph.getName());
        JSONObject jsonObject = Graph.builder()
                .name("create_object_with_public_token_api")
                .build()
                .toJson();
        String message = createGraphWithPublicToken(jsonObject).assertStatus(403).jsonPath().getString("error.code");
        assertEquals("access_denied", message);
        message = partialUpdateGraphWithPublicToken(graph.getGraphId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403).jsonPath().getString("error.code");
        assertEquals("access_denied", message);
        putGraphByIdWithPublicToken(graph.getGraphId(), jsonObject).assertStatus(403).jsonPath().getString("error.code");
        assertEquals("access_denied", message);
        deleteGraphWithPublicToken(graph.getGraphId()).assertStatus(403).jsonPath().getString("error.code");
        assertEquals("access_denied", message);
    }

    @DisplayName("Копирование графа по Id")
    @TmsLink("642640")
    @Test
    public void copyGraphByIdTest() {
        Graph graph = createGraph("graph_clone_test_api");
        String cloneName = graph.getName() + "-clone";
        copyGraphById(graph.getGraphId());
        assertTrue(isGraphExists(cloneName));
        deleteGraphById(getGraphByNameFilter(cloneName).getGraphId());
        assertFalse(isGraphExists(cloneName));
    }

    @DisplayName("Проверка tag_list при копировании")
    @TmsLink("")
    @Test
    public void copyGraphAndCheckTagListTest() {
        String graphName = "clone_graph_test_api";
        Graph graph = Graph.builder()
                .name(graphName)
                .title(graphName)
                .tagList(Arrays.asList("api_test", "test"))
                .build()
                .createObject();
        Graph cloneGraph = copyGraphById(graph.getGraphId());
        deleteGraphById(cloneGraph.getGraphId());
        assertEquals(graph.getTagList(), cloneGraph.getTagList());
    }

    @DisplayName("Частичное обновление графа по Id")
    @TmsLink("642650")
    @Test
    public void partialUpdateGraphTest() {
        Graph graph = Graph.builder()
                .name("partial_update_graph_test_api")
                .version("1.0.0")
                .damageOrderOnError(false)
                .build()
                .createObject();
        String oldGraphVersion = graph.getVersion();
        partialUpdateGraph(graph.getGraphId(), new JSONObject()
                .put("damage_order_on_error", true)).assertStatus(200);
        Graph getGraphResponse = getGraphById(graph.getGraphId());
        Boolean damageOrderOnError = getGraphResponse.getDamageOrderOnError();
        String newGraphVersion = getGraphResponse.getVersion();
        assertEquals(true, damageOrderOnError);
        assertNotEquals(oldGraphVersion, newGraphVersion);
    }


    @DisplayName("Обновление графа с указанием версии в граничных значениях")
    @TmsLink("642680")
    @Test
    public void updateGraphAndGetVersion() {
        Graph graphTest = Graph.builder()
                .name("update_graph_check_version_test_api")
                .version("1.0.999")
                .build()
                .createObject();
        partialUpdateGraph(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", true));
        String currentVersion = getGraphById(graphTest.getGraphId()).getVersion();
        assertEquals("1.1.0", currentVersion);
        partialUpdateGraph(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", false)
                .put("version", "1.999.999"));
        partialUpdateGraph(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", true));
        currentVersion = getGraphById(graphTest.getGraphId()).getVersion();
        assertEquals("2.0.0", currentVersion);
        partialUpdateGraph(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", false)
                .put("version", "999.999.999"));
        String message = partialUpdateGraph(graphTest.getGraphId(), new JSONObject().put("damage_order_on_error", true))
                .assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        assertEquals("Version counter full [999, 999, 999]", message);
    }

    @DisplayName("Попытка удаления графа используемого в продукте, действии и сервисе")
    @TmsLink("642692")
    @Test
    public void deleteUsedGraph() {
        Graph mainGraph = createGraph("main_graph");
        String mainGraphId = mainGraph.getGraphId();
        partialUpdateGraph(mainGraphId, new JSONObject().put("description", "updateVersion2.0")
                .put("version", "2.0.0"));
        partialUpdateGraph(mainGraphId, new JSONObject().put("description", "updateVersion3.0")
                .put("version", "3.0.0"));

        Product product = Product.builder()
                .name("product_for_graph_test_api")
                .graphId(mainGraphId)
                .build().createObject();

        Service service = Service.builder()
                .name("service_for_graph_test_api")
                .graphId(mainGraphId)
                .build().createObject();

        Action action = Action.builder()
                .name("action_for_graph_test_api")
                .graphId(mainGraphId)
                .build().createObject();

        String deleteResponse = getDeleteResponse(mainGraphId).assertStatus(400).extractAs(ErrorMessage.class).getMessage();
        String version = StringUtils.findByRegex("version: ([0-9.]+)\\)", deleteResponse);
        assertEquals("1.0.0", version);
        assertEquals(String.format("Нельзя удалить граф: %s. Он используется:\nProduct: (name: %s, version: 1.0.0)\nAction: (name: %s, version: 1.0.0)\nService: (name: %s, version: 1.0.0)",
                mainGraph.getName(), product.getName(), action.getName(), service.getName()), deleteResponse);
    }

    @DisplayName("Удаление графа")
    @TmsLink("642697")
    @Test
    public void deleteGraph() {
        Graph graph = createGraph("delete_graph_test_api");
        deleteGraphById(graph.getGraphId());
    }

    @Test
    @Disabled
    @DisplayName("Загрузка Graph в GitLab")
    @TmsLink("821972")
    public void dumpToGitlabGraph() {
        String graphName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Graph graph = Graph.builder()
                .name(graphName)
                .title(graphName)
                .version("1.0.0")
                .build()
                .createObject();
        String tag = "graph_" + graphName + "_" + graph.getVersion();
        Response response = dumpGraphToBitbucket(graph.getGraphId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @Disabled
    @DisplayName("Выгрузка Graph из GitLab")
    @TmsLink("1028898")
    public void loadFromGitlabGraph() {
        String graphName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        Graph graph = Graph.builder()
                .name(graphName)
                .title(graphName)
                .version("1.0.0")
                .build()
                .createObject();
        Response response = dumpGraphToBitbucket(graph.getGraphId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        deleteGraphById(graph.getGraphId());
        String path = "graph_" + graphName + "_" + graph.getVersion();
        loadGraphFromBitbucket(new JSONObject().put("path", path));
        assertTrue(isGraphExists(graphName));
        deleteGraphById(getGraphByNameFilter(graphName).getGraphId());
        assertFalse(isGraphExists(graphName));
    }

    @DisplayName("Передача и получение значений allowed_developers")
    @TmsLink("1027311")
    @Test
    public void getAllowedDevelopers() {
        List<String> allowedDevelopersList = Arrays.asList("allowed_developer1", "allowed_developer2");
        Graph graph = Graph.builder()
                .name("graph_get_allowed_developers_test_api")
                .allowedDevelopers(allowedDevelopersList)
                .build()
                .createObject();
        Graph createdGraph = getGraphById(graph.getGraphId());
        assertEquals(allowedDevelopersList, createdGraph.getAllowedDevelopers());
    }

    @DisplayName("Передача и получение значений restricted_developers")
    @TmsLink("1027313")
    @Test
    public void getRestrictedDevelopers() {
        List<String> restrictedDevelopersList = Arrays.asList("restricted_developer1", "restricted_developer2");
        Graph graph = Graph.builder()
                .name("graph_get_restricted_developers_test_api")
                .restrictedDevelopers(restrictedDevelopersList)
                .build()
                .createObject();
        Graph createdGraph = getGraphById(graph.getGraphId());
        assertEquals(restrictedDevelopersList, createdGraph.getRestrictedDevelopers());
    }

    @DisplayName("Создание графа с модификацией и способом изменения delete")
    @TmsLink("1050995")
    @Test
    public void createGraphWithUpdateTypeDelete() {
        Modification mod = Modification.builder()
                .name("mod1")
                .data(new LinkedHashMap<String, Object>() {{
                    put("test", "test");
                }})
                .envs(Arrays.asList(Env.TEST, Env.DEV))
                .order(1)
                .path("")
                .updateType(UpdateType.DELETE)
                .rootPath(RootPath.UI_SCHEMA)
                .build();
        Graph graph = Graph.builder()
                .name("create_graph_with_update_type_delete")
                .modifications(Collections.singletonList(mod))
                .build()
                .createObject();
        Graph actualGraph = getGraphById(graph.getGraphId());
        assertEquals(UpdateType.DELETE, actualGraph.getModifications().get(0).getUpdateType());
    }

    @DisplayName("Копирование графа с модификацией")
    @TmsLink("1050996")
    @Test
    public void copyGraphWithModification() {
        String modName = "mod1";
        Modification mod = Modification.builder()
                .name(modName)
                .data(new LinkedHashMap<String, Object>() {{
                    put("test", "test");
                }})
                .envs(Arrays.asList(Env.TEST, Env.DEV))
                .order(1)
                .path("")
                .updateType(UpdateType.DELETE)
                .rootPath(RootPath.UI_SCHEMA)
                .build();
        String name = "copy_graph_with_modification";
        Graph graph = Graph.builder()
                .name("copy_graph_with_modification")
                .modifications(Collections.singletonList(mod))
                .build()
                .createObject();
        copyGraphById(graph.getGraphId());
        String copyGraphId = getGraphByNameFilter(name + "-clone").getGraphId();
        Graph copyGraph = getGraphById(copyGraphId);
        assertEquals(modName, copyGraph.getModifications().get(0).getName());
        deleteGraphById(copyGraphId);
    }

    @DisplayName("Создание графа с модификацией в среде TEST_LT")
    @TmsLink("1378600")
    @Test
    public void createGraphWithModInTestLtEnv() {
        Modification mod = Modification.builder()
                .name("mod1")
                .data(new LinkedHashMap<String, Object>() {{
                    put("test", "test");
                }})
                .envs(Collections.singletonList(Env.TEST_LT))
                .order(1)
                .path("")
                .updateType(UpdateType.DELETE)
                .rootPath(RootPath.UI_SCHEMA)
                .build();
        Graph graph = Graph.builder()
                .name("create_graph_with_mod_in_test_env")
                .modifications(Collections.singletonList(mod))
                .build()
                .createObject();
        Graph actualGraph = getGraphById(graph.getGraphId());
        assertEquals(Env.TEST_LT, actualGraph.getModifications().get(0).getEnvs().get(0));
    }

    @DisplayName("Создание графа с не версионным полем default_item")
    @TmsLink("1509589")
    @Test
    public void createGraphWithDefaultItemTest() {
        Graph graph = Graph.builder()
                .name("create_graph_with_default_item_test_api")
                .build()
                .createObject();
        String expectedVersion = graph.getVersion();
        assertTrue(Objects.nonNull(graph.getDefaultItem()));
        partialUpdateGraph(graph.getGraphId(), new JSONObject().put("default_item", new JSONObject().put("test", "api")));
        assertEquals(expectedVersion, getGraphById(graph.getGraphId()).getVersion());
    }
}
