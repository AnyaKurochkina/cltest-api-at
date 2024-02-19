package api.cloud.productCatalog.graph;

import api.Tests;
import core.helper.StringUtils;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import models.cloud.productCatalog.pythonTemplate.PythonTemplate;
import models.cloud.productCatalog.template.Template;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static core.helper.StringUtils.format;
import static models.cloud.productCatalog.graph.GraphItem.getGraphItemFromJsonTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.Jinja2Steps.createJinja;
import static steps.productCatalog.PythonTemplateSteps.createPythonTemplateByName;
import static steps.productCatalog.TemplateSteps.createTemplateByName;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphItemTest extends Tests {

    @Test
    @TmsLink("SOUL-7700")
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
        graphItem.setSourceVersionCalculated("1.0.0");
        assertEquals(graphItem, graph.getGraph().get(0));
    }

    @Test
    @TmsLink("SOUL-7701")
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
        graphItem.setSourceVersionCalculated("1.0.0");
        assertEquals(graphItem, graph.getGraph().get(0));
    }

    @Test
    @TmsLink("SOUL-7702")
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
        graphItem.setSourceVersionCalculated("1.0.0");
        assertEquals(graphItem, graph.getGraph().get(0));
    }

    @Test
    @TmsLink("SOUL-9171")
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
        graphItem.setSourceVersionCalculated("1.0.0");
        assertEquals(graphItem, graph.getGraph().get(0));
    }

    @Test
    @TmsLink("SOUL-7703")
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
    @TmsLink("SOUL-7704")
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
}
