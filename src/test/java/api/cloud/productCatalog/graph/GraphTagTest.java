package api.cloud.productCatalog.graph;

import api.Tests;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.GraphSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphTagTest extends Tests {

    @DisplayName("Добавление/Удаление списка Тегов в графах")
    @TmsLinks({@TmsLink("1701361"), @TmsLink("1701362")})
    @Test
    public void addTagGraphTest() {
        List<String> tagList = Arrays.asList("test_api", "test_api2");
        Graph graph1 = createGraph("add_tag1_test_api");
        Graph graph2 = createGraph("add_tag2_test_api");
        addTagListToGraph(tagList, graph1.getName(), graph2.getName());
        assertEquals(tagList, getGraphById(graph1.getGraphId()).getTagList());
        assertEquals(tagList, getGraphById(graph2.getGraphId()).getTagList());
        removeTagListFromGraph(tagList, graph1.getName(), graph2.getName());
        assertTrue(getGraphById(graph1.getGraphId()).getTagList().isEmpty());
        assertTrue(getGraphById(graph2.getGraphId()).getTagList().isEmpty());
    }

    @DisplayName("Проверка значения поля tag_list в графах")
    @TmsLink("1701842")
    @Test
    public void checkGraphTagListValueTest() {
        List<String> tagList = Arrays.asList("graph_tag_test_value", "graph_tag_test_value2");
        Graph graph = createGraph(Graph.builder()
                .name("at_api_check_graph_tag_list_value")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build());
        List<String> graphTagList = graph.getTagList();
        assertTrue(tagList.size() == graphTagList.size() && tagList.containsAll(graphTagList) && graphTagList.containsAll(tagList));
        tagList = Collections.singletonList("graph_tag_test_value3");
        partialUpdateGraph(graph.getGraphId(), new JSONObject().put("tag_list", tagList));
        Graph createdGraph = getGraphById(graph.getGraphId());
        AssertUtils.assertEqualsList(tagList, createdGraph.getTagList());
    }

    @DisplayName("Проверка не версионности поля tag_list в графах")
    @TmsLink("1701851")
    @Test
    public void checkGraphTagListVersioning() {
        List<String> tagList = Arrays.asList("graph_tag_test_value", "graph_tag_test_value2");
        Graph graph = createGraph(Graph.builder()
                .name("at_api_graph_check_tag_list_versioning")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(tagList)
                .build());
        tagList = Collections.singletonList("graph_tag_test_value3");
        partialUpdateGraph(graph.getGraphId(), new JSONObject().put("tag_list", tagList));
        Graph updatedGraph = getGraphById(graph.getGraphId());
        assertEquals("1.0.0", updatedGraph.getVersion());
    }
}

