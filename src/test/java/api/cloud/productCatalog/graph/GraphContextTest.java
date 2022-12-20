package api.cloud.productCatalog.graph;


import api.Tests;
import core.helper.Configure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
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
import static steps.productCatalog.GraphSteps.getGraphByIdContext;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphContextTest extends Tests {
    Project project;

    public GraphContextTest() {
        project = Project.builder()
                .isForOrders(true)
                .build()
                .createObject();
    }

    @DisplayName("Получение графа c модификаторами для текущей среды по контексту")
    @TmsLink("1353529")
    @Test
    public void getGraphWithModsToCurrentEnvByContextTest() {
        Env env = Env.DEV;
        if (Configure.ENV.equals("ift")) {
            env = Env.TEST;
        }
        String jsonData = "dev_title";
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envs(Collections.singletonList(env))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(jsonData)
                .build();
        String uiData = "ui_schema_dev_title";
        Modification uiSchema = Modification.builder()
                .name("ui_schema_dev_mod")
                .envs(Collections.singletonList(env))
                .order(2)
                .path("title")
                .rootPath(RootPath.UI_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(uiData)
                .build();
        String dataStatic = "static_data_dev_title";
        Modification staticData = Modification.builder()
                .name("static_data_dev_mod")
                .envs(Collections.singletonList(env))
                .order(3)
                .path("title")
                .rootPath(RootPath.STATIC_DATA)
                .updateType(UpdateType.REPLACE)
                .data(dataStatic)
                .build();
        Graph graph = Graph.builder()
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
                .build()
                .createObject();
        Graph createdGraph = getGraphByIdContext(project.getId(), graph.getGraphId());
        assertEquals(jsonData, createdGraph.getJsonSchema().get("title"));
        assertEquals(uiData, createdGraph.getUiSchema().get("title"));
        assertEquals(dataStatic, createdGraph.getStaticData().get("title"));
    }

    @DisplayName("Получение графа c модификаторами не для текущей среды по контексту")
    @TmsLink("1353531")
    @Test
    public void getGraphWithModsToAnotherEnvByContextTest() {
        Env env = Env.TEST;
        if (Configure.ENV.equals("ift")) {
            env = Env.DEV;
        }
        String jsonData = "dev_title";
        Modification jsonSchema = Modification.builder()
                .name("json_schema_dev_mod")
                .envs(Collections.singletonList(env))
                .order(1)
                .path("title")
                .rootPath(RootPath.JSON_SCHEMA)
                .updateType(UpdateType.REPLACE)
                .data(jsonData)
                .build();
        String titleValue = "default";
        Graph graph = Graph.builder()
                .name("get_graph_with_mods_another_env_by_context_test_api")
                .version("1.0.0")
                .modifications(Arrays.asList(jsonSchema))
                .jsonSchema(new LinkedHashMap<String, Object>() {{
                    put("title", titleValue);
                }})
                .build()
                .createObject();
        Graph createdGraph = getGraphByIdContext(project.getId(), graph.getGraphId());
        assertEquals(titleValue, createdGraph.getJsonSchema().get("title"));
    }
}
