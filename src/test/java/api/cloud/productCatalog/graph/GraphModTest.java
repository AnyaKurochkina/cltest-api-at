package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.http.QueryBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.productCatalog.Env;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.Modification;
import models.cloud.productCatalog.graph.RootPath;
import models.cloud.productCatalog.graph.UpdateType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.GraphSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphModTest extends Tests {

    @DisplayName("Получение графа c модификаторами без query параметров")
    @TmsLink("1458128")
    @Test
    public void getGraphWithModsWithOutContextTest() {
        String jsonData = "dev_title";
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envNames(Collections.singletonList("IFT"))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(jsonData)
                .build();
        String uiData = "ui_schema_dev_title";
        Modification uiSchema = Modification.builder()
                .name("ui_schema_dev_mod")
                .envs(Collections.singletonList(Env.TEST))
                .order(2)
                .path("title")
                .rootPath(RootPath.UI_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(uiData)
                .build();
        String expectedTitle = "default";
        Graph graph = createGraph(Graph.builder()
                .name("get_graph_with_mod_with_out_query_param__test_api")
                .version("1.0.0")
                .modifications(Arrays.asList(jsonSchema, uiSchema))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .uiSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .build());
        Graph createdGraph = getGraphById(graph.getGraphId());
        assertEquals(expectedTitle, createdGraph.getJsonSchema().get("title"));
        assertEquals(expectedTitle, createdGraph.getUiSchema().get("title"));
    }

    @DisplayName("Получение графа по типу среды не совпадающей с типом среды модификатора")
    @TmsLink("1458129")
    @Test
    public void getGraphByEnvTypeNotEqualsModEnvTypeTest() {
        String jsonData = "dev_title";
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envs(Collections.singletonList(Env.TEST))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(jsonData)
                .build();
        String expectedTitle = "default";
        Graph graph = createGraph(Graph.builder()
                .name("get_graph_by_env_type_not_equals_mod_env_type_test_api")
                .version("1.0.0")
                .modifications(Collections.singletonList(jsonSchema))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .build());
        Graph createdGraph = getGraphByIdWithQueryParams(graph.getGraphId(), new QueryBuilder().add("env", "dev"));
        assertEquals(expectedTitle, createdGraph.getJsonSchema().get("title"));
    }

    @DisplayName("Получение графа по среде совпадающей со средой модификатора")
    @TmsLink("1458167")
    @Test
    public void getGraphByEnvEqualsModEnvTest() {
        String jsonData = "graph_env_equals_mod_env";
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envNames(Arrays.asList("DSO", "LT", "IFT"))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(jsonData)
                .build();
        String expectedTitle = "default";
        Graph graph = createGraph(Graph.builder()
                .name("get_graph_by_env_equals_mod_env_test_api")
                .version("1.0.0")
                .modifications(Collections.singletonList(jsonSchema))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .build());
        Graph createdGraph = getGraphByIdWithQueryParams(graph.getGraphId(), new QueryBuilder().add("env_name", "dso"));
        assertEquals(jsonData, createdGraph.getJsonSchema().get("title"));
    }

    @DisplayName("Получение графа по типу среды совпадающей с типом среды модификатора и по среде отличающейся от среды модификатора")
    @TmsLink("1458235")
    @Test
    public void getGraphByEnvTypeEqualsModEnvTypeAndEnvNotEqualsModEnvTest() {
        String jsonData = "graph_env_equals_mod_env";
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envNames(Arrays.asList("DSO", "LT", "IFT"))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(jsonData)
                .build();
        String uiData = "ui_schema_dev_title";
        Modification uiSchema = Modification.builder()
                .name("ui_schema_dev_mod")
                .envs(Collections.singletonList(Env.TEST))
                .order(2)
                .path("title")
                .rootPath(RootPath.UI_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(uiData)
                .build();
        String expectedTitle = "default";
        Graph graph = createGraph(Graph.builder()
                .name("get_graph_by_env_type_equals_mod_env_type_and_env_not_equals_mod_env")
                .version("1.0.0")
                .modifications(Arrays.asList(jsonSchema, uiSchema))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .uiSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .build());
        Graph createdGraph = getGraphByIdWithQueryParams(graph.getGraphId(), new QueryBuilder().add("env", "test").add("env_name", "migr"));
        assertEquals(expectedTitle, createdGraph.getJsonSchema().get("title"));
        assertEquals(uiData, createdGraph.getUiSchema().get("title"));
    }

    @DisplayName("Получение графа по типу среды и среды совпадающими с типом среды и среды модификатора")
    @TmsLink("1458388")
    @Test
    public void getGraphByEnvTypeAndEnvEqualsModEnvTypeAndEnvTest() {
        String jsonData = "graph_env_equals_mod_env";
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envNames(Arrays.asList("DSO", "LT", "IFT"))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(jsonData)
                .build();
        String uiData = "ui_schema_dev_title";
        Modification uiSchema = Modification.builder()
                .name("ui_schema_dev_mod")
                .envs(Collections.singletonList(Env.TEST))
                .order(2)
                .path("title")
                .rootPath(RootPath.UI_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(uiData)
                .build();
        String expectedTitle = "default";
        Graph graph = createGraph(Graph.builder()
                .name("get_graph_by_env_type_and_type_equals_mod_env_type_and_env")
                .version("1.0.0")
                .modifications(Arrays.asList(jsonSchema, uiSchema))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .uiSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .build());
        Graph createdGraph = getGraphByIdWithQueryParams(graph.getGraphId(), new QueryBuilder().add("env", "test").add("env_name", "lt"));
        assertEquals(jsonData, createdGraph.getJsonSchema().get("title"));
        assertEquals(uiData, createdGraph.getUiSchema().get("title"));
    }

    @DisplayName("Получение графа с несуществующим ключем в модификациях")
    @TmsLink("SOUL-8276")
    @Test
    public void getGraphWithNotExistKeysInModificationTest() {
        String key = RandomStringUtils.randomAlphabetic(5).toLowerCase();
        Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix("DEV"))
                .build()
                .createObject();
        String jsonData = "dev_title";
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envNames(Collections.singletonList("DEV"))
                .order(1)
                .path(format("{}.sdf", key))
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(jsonData)
                .build();
        Graph graph = createGraph(Graph.builder()
                .name("get_graph_with_not_exist_key_in_mod_test_api")
                .version("1.0.0")
                .modifications(Collections.singletonList(jsonSchema))
                .build());
        String errorMsg = getResponseGraphByIdContext(project.getId(), graph.getGraphId()).extractAs(ErrorMessage.class).getMessage();
        assertEquals(format("No {} key in {} dictionary for schema (json_schema)", key), errorMsg);
    }
}
