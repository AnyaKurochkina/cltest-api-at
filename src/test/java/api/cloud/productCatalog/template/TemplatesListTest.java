package api.cloud.productCatalog.template;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.template.Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static core.helper.Configure.getAppProp;
import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.partialUpdateGraph;
import static steps.productCatalog.ProductCatalogSteps.isSorted;
import static steps.productCatalog.TemplateSteps.*;

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
    public void getTemplateListTest() {
        List<Template> list = getTemplateList();
        assertTrue(isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка шаблонов")
    @TmsLink("682827")
    @Test
    public void getMeta() {
        String str = getTemplatesList().getMeta().getNext();
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
                .name(templateName)
                .build()
                .createObject();
        String expectedNodeName = "graph_item_test_api";
        JSONObject graphItem = GraphItem.builder()
                .name(expectedNodeName)
                .templateId(template.getId())
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
        String nodeName = getNodeListUsedTemplate(template.getId()).jsonPath().getString("[0].nodes[0].node_name");
        assertEquals(expectedNodeName, nodeName);
    }

    @DisplayName("Получение списка шаблонов c Тегами")
    @TmsLink("1709895")
    @Test
    public void getTemplateListWithTagListTest() {
        Template.builder()
                .name("at_api_template_check_tag_list_versioning")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(Collections.singletonList("api_test"))
                .build()
                .createObject();
        List<Template> templateList = getTemplateListByFilter("with_tag_list", true);
        templateList.forEach(x -> assertNotNull(x.getTagList()));
    }

    @DisplayName("Получение списка шаблонов отфильтрованном по Тегам с полным совпадением")
    @TmsLink("1709931")
    @Test
    public void getTemplateListFilteredByTagsTest() {
        String tag1 = "api_test";
        String tag2 = "complete";
        Template.builder()
                .name("at_api_template_check_tag_list_filtered_by_tags")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(Arrays.asList(tag1, tag2))
                .build()
                .createObject();
        Template.builder()
                .name("template_for_list_filtered_by_tags")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(Arrays.asList(tag1, tag2))
                .build()
                .createObject();
        List<Template> templateList = getTemplateListByFilters("with_tag_list=true", "tags_complete_match=true",
                String.format("tags=%s,%s", tag1, tag2));
        assertEquals(2, templateList.size());
        templateList.forEach(x-> assertEquals(x.getTagList(), Arrays.asList(tag1, tag2)));
    }

    @DisplayName("Получение списка шаблонов отфильтрованном по Тегам с не полным совпадением")
    @TmsLink("1709934")
    @Test
    public void getTemplateListFilteredByTagsAndCompleteMatchFalseTest() {
        Template.builder()
                .name("at_api_action_check_tag_list_filtered_by_tags")
                .title("AT API Product")
                .version("1.0.0")
                .tagList(Arrays.asList("api_test", "api_test_action"))
                .build()
                .createObject();
        List<Template> templateList = getTemplateListByFilters("with_tag_list=true", "tags_complete_match=false", "tags=api_test");
        templateList.forEach(x -> assertTrue(x.getTagList().stream().anyMatch(y -> y.equals("api_test"))));
    }
}
