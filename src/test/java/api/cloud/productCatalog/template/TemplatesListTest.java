package api.cloud.productCatalog.template;

import api.Tests;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Template;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import java.util.ArrayList;
import java.util.List;

import static core.helper.Configure.getAppProp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.GraphSteps.partialUpdateGraph;
import static steps.productCatalog.TemplateSteps.getNodeListUsedTemplate;

@Epic("Продуктовый каталог")
@Feature("Шаблоны")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class TemplatesListTest extends Tests {
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/templates/",
            "productCatalog/templates/createTemplate.json");

    @DisplayName("Получение списка шаблонов")
    @TmsLink("643551")
    @Test
    public void getTemplateList() {
        List<ItemImpl> list = steps.getProductObjectList(GetTemplateListResponse.class);
        assertTrue(steps.isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка шаблонов")
    @TmsLink("682827")
    @Test
    public void getMeta() {
        String str = steps.getMeta(GetTemplateListResponse.class).getNext();
        String url = getAppProp("url.kong");
        if (!(str == null)) {
            assertTrue(str.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка узлов использующих шаблоны")
    @TmsLink("1287213")
    @Test
    public void getNodeUsedTemplateTest() {
        String templateName = "create_template_used_in_graph_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        String expectedNodeName = "graph_item_test_api";
        JSONObject graphItem = GraphItem.builder()
                .name(expectedNodeName)
                .templateId(template.getTemplateId())
                .build()
                .toJson();
        Graph graph = Graph.builder()
                .name("create_graph_used_in_graph_test_api")
                .title("create_graph_used_in_graph_test_api")
                .build()
                .createObject();
        List<JSONObject> list = new ArrayList<>();
        list.add(graphItem);
        JSONObject obj = new JSONObject().put("graph", list);
        partialUpdateGraph(graph.getGraphId(), obj);
        String nodeName = getNodeListUsedTemplate(template.getTemplateId()).jsonPath().getString("[0].nodes[0].node_name");
        assertEquals(expectedNodeName, nodeName);
    }
}
