package api.cloud.productCatalog.graph;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.*;

@Epic("Продуктовый каталог")
@Feature("Графы")
public class GraphByNameTest extends GraphBaseTest {

    @DisplayName("Получение графа по имени")
    @TmsLink("1505891")
    @Test
    public void getGraphByNameTest() {
        String graphName = "get_graph_by_name_example_test_api";
        Graph graph = createGraph(graphName);
        Graph getGraph = getGraphByName(graphName);
        assertEquals(graph, getGraph);
    }

    @DisplayName("Обновление графа по имени")
    @TmsLink("1505948")
    @Test
    public void updateGraphByNameTest() {
        String graphName = "update_graph_by_name_test_api";
        createGraph(graphName);
        partialUpdateGraphByName(graphName, new JSONObject().put("damage_order_on_error", true));
        assertEquals("1.0.1", getGraphByName(graphName).getVersion(), "Версии не совпадают");
    }

    @Test
    @DisplayName("Удаление графа по имени")
    @TmsLink("1505952")
    public void deleteGraphByNameTest() {
        String graphName = "graph_delete_by_name_test_api";
        if (isGraphExists(graphName)) {
            deleteGraphByName(graphName);
        }
        JSONObject jsonObject = createGraphModel(graphName)
                .toJson();
        createGraph(jsonObject).assertStatus(201);
        deleteGraphByName(graphName);
        assertFalse(isGraphExists(graphName));
    }

    @DisplayName("Копирование графа по имени")
    @TmsLink("1505960")
    @Test
    public void copyGraphByNameTest() {
        String graphName = "clone_graph_by_name_test_api";
        createGraph(graphName);
        Graph cloneGraph = copyGraphByName(graphName);
        String cloneName = cloneGraph.getName();
        assertTrue(isGraphExists(cloneName), "Граф не существует");
        deleteGraphByName(cloneName);
        assertFalse(isGraphExists(cloneName), "Граф существует");
    }

    @DisplayName("Проверка tag_list при копировании графа v2")
    @TmsLink("SOUL-7003")
    @Test
    public void copyGraphAndCheckTagListV2Test() {
        String graphName = "clone_graph_v2_test_api";
        Graph graph = createGraph(Graph.builder()
                .name(graphName)
                .title(graphName)
                .tagList(Arrays.asList("api_test", "test"))
                .build());
        Graph cloneGraph = copyGraphByName(graphName);
        deleteGraphById(cloneGraph.getGraphId());
        assertEquals(graph.getTagList(), cloneGraph.getTagList());
    }
}
