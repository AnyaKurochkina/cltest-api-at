package api.cloud.productCatalog.graph;


import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.productCatalog.Env;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.Modification;
import models.cloud.productCatalog.graph.RootPath;
import models.cloud.productCatalog.graph.UpdateType;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.GraphSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphContextTest extends Tests {
    Project project;

    public GraphContextTest() {
        project = Project.builder()
                .projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
                .isForOrders(true)
                .build()
                .createObject();
    }

    @DisplayName("Получение графа c модификаторами для текущей среды по контексту")
    @TmsLink("1353529")
    @Test
    public void getGraphWithModsToCurrentEnvByContextTest() {
        String jsonData = "dev_title";
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envs(Collections.singletonList(Env.DEV))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(jsonData)
                .build();
        String uiData = "ui_schema_dev_title";
        Modification uiSchema = Modification.builder()
                .name("ui_schema_dev_mod")
                .envs(Collections.singletonList(Env.DEV))
                .order(2)
                .path("title")
                .rootPath(RootPath.UI_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(uiData)
                .build();
        String dataStatic = "static_data_dev_title";
        Modification staticData = Modification.builder()
                .name("static_data_dev_mod")
                .envs(Collections.singletonList(Env.DEV))
                .order(3)
                .path("title")
                .rootPath(RootPath.STATIC_DATA)
                .updateType(UpdateType.REPLACE)
                .data(dataStatic)
                .build();
        Graph graph = createGraph(Graph.builder()
                .name("get_graph_by_context_test_api")
                .version("1.0.0")
                .modifications(Arrays.asList(jsonSchema, uiSchema, staticData))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", "default");
                }})
                .uiSchema(new LinkedHashMap<String, Object>() {{
                    put("title", "default");
                }})
                .staticData(new LinkedHashMap<String, Object>() {{
                    put("title", "default");
                }})
                .build());
        Graph createdGraph = getGraphByIdContext(project.getId(), graph.getGraphId());
        assertEquals(jsonData, createdGraph.getJsonSchema().get("title"));
        assertEquals(uiData, createdGraph.getUiSchema().get("title"));
        assertEquals(dataStatic, createdGraph.getStaticData().get("title"));
    }

    @DisplayName("Получение графа c модификаторами не для текущей среды по контексту")
    @TmsLink("1353531")
    @Test
    public void getGraphWithModsToAnotherEnvByContextTest() {
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
        String titleValue = "default";
        Graph graph = createGraph(Graph.builder()
                .name("get_graph_with_mods_another_env_by_context_test_api")
                .version("1.0.0")
                .modifications(Collections.singletonList(jsonSchema))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", titleValue);
                }})
                .build());
        Graph createdGraph = getGraphByIdContext(project.getId(), graph.getGraphId());
        assertEquals(titleValue, createdGraph.getJsonSchema().get("title"));
    }

    @DisplayName("Получение графа c модификаторами без контекста")
    @TmsLink("1532316")
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
                .envNames(Collections.singletonList("IFT"))
                .order(2)
                .path("title")
                .rootPath(RootPath.UI_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(uiData)
                .build();
        String expectedTitle = "default";
        Graph graph = createGraph(Graph.builder()
                .name("get_graph_with_out_context_test_api")
                .version("1.0.0")
                .modifications(Arrays.asList(jsonSchema, uiSchema))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .uiSchema(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .staticData(new LinkedHashMap<String, Object>() {{
                    put("title", expectedTitle);
                }})
                .build());
        Graph createdGraph = getGraphById(graph.getGraphId());
        assertEquals(expectedTitle, createdGraph.getJsonSchema().get("title"));
        assertEquals(expectedTitle, createdGraph.getUiSchema().get("title"));
    }


    @Test
    @TmsLink("1292180")
    @DisplayName("Добавление модификатора с типом delete")
    public void addDeleteTypeModifier() {
        Modification jsonSchemaMod = Modification.builder()
                .name("json_schema_dev_mod")
                .envs(Collections.singletonList(Env.DEV))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.DELETE)
                .build();
        Modification uiSchemaMod = Modification.builder()
                .name("ui_schema_dev_mod")
                .envs(Collections.singletonList(Env.DEV))
                .order(2)
                .path("ui:order")
                .rootPath(RootPath.UI_SCHEMA)
                .updateType(UpdateType.DELETE)
                .build();
        Modification staticDataMod = Modification.builder()
                .name("static_data_dev_mod")
                .envs(Collections.singletonList(Env.DEV))
                .order(3)
                .path("domain")
                .rootPath(RootPath.STATIC_DATA)
                .updateType(UpdateType.DELETE)
                .build();
        Graph graph = createGraph(Graph.builder()
                .name("at_api_get_graph_with_delete_modifiers")
                .version("1.0.0")
                .modifications(Arrays.asList(jsonSchemaMod, uiSchemaMod, staticDataMod))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", "default");
                }})
                .uiSchema(new LinkedHashMap<String, Object>() {{
                    put("ui:order", "default");
                }})
                .staticData(new LinkedHashMap<String, Object>() {{
                    put("domain", "default");
                }})
                .build());
        Graph createdGraph = getGraphByIdContext(project.getId(), graph.getGraphId());
        assertEquals(0, createdGraph.getJsonSchema().size());
        assertEquals(0, createdGraph.getUiSchema().size());
        assertEquals(0, createdGraph.getStaticData().size());
    }
}
