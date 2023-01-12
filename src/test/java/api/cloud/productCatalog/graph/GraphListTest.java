package api.cloud.productCatalog.graph;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.GetGraphList;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.helper.Configure.getAppProp;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.*;
import static steps.productCatalog.ProductCatalogSteps.isSorted;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Графы")
@DisabledIfEnv("prod")
public class GraphListTest extends Tests {

    @DisplayName("Получение списка графов по title используя multisearch")
    @TmsLink("806274")
    @Test
    public void getGraphListByTitleWithMutisearch() {
        String graphName = "create_graph_example_for_get_list_by_title_with_multisearch_test_api";
        String graphTitle = "graph_title";
        Graph.builder()
                .name(graphName)
                .title(graphTitle)
                .build()
                .createObject();
        List<Graph> list = getGraphListWithMultiSearch(graphTitle);
        list.forEach(x -> assertTrue(x.getTitle().contains(graphTitle)));
    }

    @DisplayName("Получение списка графа")
    @TmsLink("642539")
    @Test
    public void getGraphsListTest() {
        List<Graph> list = getGraphList();
        assertTrue(isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка графа")
    @TmsLink("679029")
    @Test
    public void getMeta() {
        String str = getMetaGraphList().getNext();
        String url = getAppProp("url.kong");
        if (!(str == null)) {
            assertTrue(str.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка версий графа по id")
    @TmsLink("821973")
    @Test
    public void getGraphVersionListById() {
        String graphName = "create_graph_example_for_get_version_list_by_id_test_api";
        String graphTitle = "graph_title";
        Graph graph = Graph.builder()
                .name(graphName)
                .title(graphTitle)
                .build()
                .createObject();
        String graphId = graph.getGraphId();
        partialUpdateGraph(graphId, new JSONObject().put("title", "title_update"));
        partialUpdateGraph(graphId, new JSONObject().put("title", "title_update2"));
        partialUpdateGraph(graphId, new JSONObject().put("title", "title_update3"));
        Graph getGraphResponse = getGraphById(graphId);
        List<String> versionList = getGraphResponse.getVersionList();
        List<String> actualVersionList = getGraphVersionList(graphId).jsonPath().getList("");
        assertEquals(versionList, actualVersionList);
    }

    @DisplayName("Проверка присутствия поля icon_url для всех нод")
    @TmsLink("1027309")
    @Test
    public void getIcon() {
        List<Graph> productObjectList = getGraphList();
        for (Graph graph : productObjectList) {
            Graph getGraph = getGraphById(graph.getGraphId());
            if (!getGraph.getGraph().isEmpty()) {
                List<GraphItem> graphItemList = getGraph.getGraph();
                for (GraphItem graphItem : graphItemList) {
                    assertNotNull(graphItem.getIconUrl(), String.format("У ноды графа %s поле icon_url is null", getGraph.getName()));
                    if (graphItem.getIconUrl().isEmpty()) {
                        assertNull(graphItem.getIconStoreId(), "icon_store_id должен быть null");
                    }
                }
            }
        }
    }

    @DisplayName("Получение списка графа по фильтру ID")
    @TmsLink("1044120")
    @Test
    public void getGraphsById() {
        String graphName = "get_graph_list_by_id_filter_test_api";
        String graphTitle = "get_graph_list_by_id_filter_test_api";
        Graph graph = Graph.builder()
                .name(graphName)
                .title(graphTitle)
                .build()
                .createObject();
        List<Graph> graphList = getGraphListById(graph.getGraphId()).extractAs(GetGraphList.class).getList();
        assertEquals(1, graphList.size());
        Graph listItem = graphList.get(0);
        assertEquals(graphName, listItem.getName());
    }

    @DisplayName("Получение списка графа по фильтру несколько ID")
    @TmsLink("1044122")
    @Test
    public void getGraphsByIds() {
        Graph firstGraph = Graph.builder()
                .name("first_graph_list_by_ids_filter_test_api")
                .title("first_graph_list_by_ids_filter_test_api")
                .build()
                .createObject();
        Graph secondGraph = Graph.builder()
                .name("second_graph_list_by_ids_filter_test_api")
                .title("second_graph_list_by_ids_filter_test_api")
                .build()
                .createObject();
        List<Graph> graphList = getGraphListByIds(firstGraph.getGraphId(), secondGraph.getGraphId());
        assertEquals(2, graphList.size());
    }

    @DisplayName("Получение списка графа по фильтру id__contains")
    @TmsLink("1044123")
    @Test
    public void getGraphsByContainsIds() {
        Graph graph = Graph.builder()
                .name("graph_list_by_id_contains_filter_test_api")
                .title("graph_list_by_id_contains_filter_test_api")
                .build()
                .createObject();
        List<Graph> graphList = getGraphListByContainsId(graph.getGraphId());
        assertEquals(1, graphList.size());
        Graph listItem = graphList.get(0);
        assertEquals(graph.getGraphId(), listItem.getGraphId());

    }
}
