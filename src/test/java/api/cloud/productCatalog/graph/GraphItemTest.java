package api.cloud.productCatalog.graph;

import core.helper.StringUtils;
import core.helper.http.AssertResponse;
import core.helper.http.QueryBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.AbstractEntity;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import models.cloud.productCatalog.pythonTemplate.PythonTemplate;
import models.cloud.productCatalog.template.Template;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.StringUtils.format;
import static models.cloud.productCatalog.graph.GraphItem.getGraphItemFromJsonTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.Jinja2Steps.createJinja;
import static steps.productCatalog.PythonTemplateSteps.createPythonTemplateByName;
import static steps.productCatalog.TemplateSteps.createTemplateByName;
import static steps.productCatalog.TemplateSteps.partialUpdateTemplate;

@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphItemTest extends GraphBaseTest {

    @Test
    @DisplayName("Создание ноды графа типа template")
    public void createGraphWithNodeTemplateSourceTypeTest() {
        Template template = createTemplateByName("template_graph_node_api_test");
        GraphItem graphItem = getGraphItemFromJsonTemplate();
        graphItem.setSourceType("template");
        graphItem.setSourceId(String.valueOf(template.getId()));
        Graph graph = createGraph(Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api")
                .graph(Collections.singletonList(graphItem))
                .build());
        assertEquals(graphItem, graph.getGraph().get(0));
    }

    @Test
    @DisplayName("Создание ноды графа типа subgraph")
    public void createGraphWithNodeSubgraphSourceTypeTest() {
        Graph subGraph = createGraph(StringUtils.getRandomStringApi(6));
        GraphItem graphItem = getGraphItemFromJsonTemplate();
        graphItem.setSourceType("subgraph");
        graphItem.setSourceId(subGraph.getGraphId());
        graphItem.setName(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api");
        Graph graph = createGraph(Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api")
                .graph(Collections.singletonList(graphItem))
                .build());
        assertEquals(graphItem, graph.getGraph().get(0));
    }

    @Test
    @DisplayName("Создание ноды графа типа jinja2")
    public void createGraphWithNodeJinjaSourceTypeTest() {
        Jinja2Template jinja = createJinja(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api");
        GraphItem graphItem = getGraphItemFromJsonTemplate();
        graphItem.setSourceType("jinja2");
        graphItem.setSourceId(jinja.getId());
        graphItem.setName(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api");
        Graph graph = createGraph(Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api")
                .graph(Collections.singletonList(graphItem))
                .build());
        assertEquals(graphItem, graph.getGraph().get(0));
    }

    @Test
    @DisplayName("Создание ноды графа типа python")
    public void createGraphWithNodePythonSourceTypeTest() {
        PythonTemplate pythonTemplate = createPythonTemplateByName(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        GraphItem graphItem = getGraphItemFromJsonTemplate();
        graphItem.setSourceType("python");
        graphItem.setSourceId(pythonTemplate.getId());
        graphItem.setName(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api");
        Graph graph = createGraph(Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api")
                .graph(Collections.singletonList(graphItem))
                .build());
        assertEquals(graphItem, graph.getGraph().get(0));
    }

    @Test
    @DisplayName("Создание ноды графа не существующего типа")
    public void createGraphWithNotExistSourceTypeTest() {
        String sourceType = "not_exist";
        Jinja2Template jinja = createJinja(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api");
        GraphItem graphItem = getGraphItemFromJsonTemplate();
        graphItem.setSourceType(sourceType);
        graphItem.setSourceId(jinja.getId());
        graphItem.setName(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api");
        JSONObject graph = Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api")
                .graph(Collections.singletonList(graphItem))
                .build()
                .toJson();
        AssertResponse.run(() -> createGraph(graph)).status(400)
                .responseContains(format("\\\"graph\\\": [ErrorDetail(string='\\\"source_type\\\": Значения {} нет среди допустимых вариантов.', code='invalid')]", sourceType));
    }

    @Test
    @DisplayName("Создание ноды графа c невалидным source_id")
    public void createGraphWithInvalidSourceIdTest() {
        Template template = createTemplateByName(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api");
        GraphItem graphItem = getGraphItemFromJsonTemplate();
        graphItem.setSourceType("jinja2");
        graphItem.setSourceId(String.valueOf(template.getId()));
        graphItem.setName(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api");
        JSONObject graph = Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api")
                .graph(Collections.singletonList(graphItem))
                .build()
                .toJson();
        AssertResponse.run(() -> createGraph(graph)).status(400)
                .responseContains(format("\\\"graph\\\": [ErrorDetail(string='\\\"non_field_errors\\\": Значение “{}” не является верным UUID-ом.', code='invalid')]", template.getId()));
    }

    @Test
    @DisplayName("Проверка source_version_calculated подграфов и шаблонов")
    public void checkSourceVersionCalculatedInSubGraphAndTemplateTest() {
        Template template = createTemplateByName("template_for_source_version_calculated_test_api");
        GraphItem templateItem = getGraphItemFromJsonTemplate();
        templateItem.setSourceType("template");
        templateItem.setSourceId(String.valueOf(template.getId()));
        templateItem.setName(template.getName());

        Graph subGraph = createGraph(Graph.builder()
                .name(StringUtils.getRandomStringApi(6))
                .build().toJson()).extractAs(Graph.class)
                .deleteMode(AbstractEntity.Mode.AFTER_CLASS);

        GraphItem subGraphItem = getGraphItemFromJsonTemplate();
        subGraphItem.setSourceType("subgraph");
        subGraphItem.setSourceId(subGraph.getGraphId());
        subGraphItem.setName("subgraph_for_source_version_calculated_test_api");

        Graph graph = createGraph(Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "test_api")
                .graph(Arrays.asList(templateItem, subGraphItem))
                .build());

        List<GraphItem> getGraphById = getGraphById(graph.getGraphId()).getGraph();
        List<GraphItem> getGraphByIdWithQuery = getGraphByIdWithQueryParams(graph.getGraphId(), new QueryBuilder().add("env", "dev")
                .add("env_name", "DEV").add("json_name", "orchestrator_json")).getGraph();
        getGraphById.forEach(x -> assertEquals("1.0.0", x.getSourceVersionCalculated()));
        getGraphByIdWithQuery.forEach(x -> assertEquals("1.0.0", x.getSourceVersionCalculated()));

        partialUpdateGraph(subGraph.getGraphId(), new JSONObject().put("lock_order_on_error", true));
        partialUpdateTemplate(template.getId(), new JSONObject().put("priority", 5));

        getGraphById = getGraphById(graph.getGraphId()).getGraph();
        getGraphByIdWithQuery = getGraphByIdWithQueryParams(graph.getGraphId(), new QueryBuilder().add("env", "dev")
                .add("env_name", "DEV").add("json_name", "orchestrator_json")).getGraph();
        getGraphById.forEach(x -> assertEquals("1.0.1", x.getSourceVersionCalculated()));
        getGraphByIdWithQuery.forEach(x -> assertEquals("1.0.1", x.getSourceVersionCalculated()));
    }
}
