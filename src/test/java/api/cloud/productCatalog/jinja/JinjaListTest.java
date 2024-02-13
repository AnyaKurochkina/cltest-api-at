package api.cloud.productCatalog.jinja;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import models.cloud.productCatalog.jinja2.UsedJinja2ObjectList;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static core.helper.Configure.getAppProp;
import static models.cloud.productCatalog.graph.GraphItem.getGraphItemFromJsonTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.Jinja2Steps.*;
import static steps.productCatalog.ProductCatalogSteps.isSorted;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaListTest extends Tests {

    @DisplayName("Получение списка jinja")
    @TmsLink("660061")
    @Test
    public void getJinjaList() {
        List<Jinja2Template> list = getJinja2List();
        assertTrue(isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка jinja")
    @TmsLink("716386")
    @Test
    public void getMeta() {
        String nextPage = getMetaJinja2List().getNext();
        String url = getAppProp("url.kong");
        if (!(nextPage == null)) {
            assertTrue(nextPage.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка объектов использующих jinja2_template по id")
    @TmsLink("SOUL-7592")
    @Test
    public void getObjectListUsedJinja2TemplateTest() {
        Jinja2Template jinja = createJinja(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        GraphItem graphItem = getGraphItemFromJsonTemplate();
        graphItem.setSourceType("jinja2");
        graphItem.setSourceId(jinja.getId());
        graphItem.setName(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        Graph graph = createGraph(Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api")
                .graph(Collections.singletonList(graphItem))
                .build());
        UsedJinja2ObjectList usedJinjaObject = getObjectListUsedJinja2Template(jinja.getId()).get(0);
        assertEquals(graph.getGraphId(), usedJinjaObject.getId());
    }

    @DisplayName("Получение списка объектов использующих jinja2_template по имени")
    @TmsLink("SOUL-7593")
    @Test
    public void getObjectListUsedJinja2TemplateByNameTest() {
        Jinja2Template jinja = createJinja(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        GraphItem graphItem = getGraphItemFromJsonTemplate();
        graphItem.setSourceType("jinja2");
        graphItem.setSourceId(jinja.getId());
        graphItem.setName(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        Graph graph = createGraph(Graph.builder()
                .name(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api")
                .graph(Collections.singletonList(graphItem))
                .build());
        UsedJinja2ObjectList usedJinjaObject = getObjectListUsedJinja2TemplateByName(jinja.getName()).get(0);
        assertEquals(graph.getName(), usedJinjaObject.getName());
    }
}
