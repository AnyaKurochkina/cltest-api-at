package ui.cloud.tests.productCatalog.tag;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.tag.TagPage;
import ui.cloud.pages.productCatalog.tag.TagsListPage;

import java.util.Collections;

import static steps.productCatalog.GraphSteps.addTagListToGraph;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ProductSteps.addTagListToProduct;
import static steps.productCatalog.ProductSteps.createProduct;
import static steps.productCatalog.TagSteps.createTag;

@Feature("Просмотр тега")
public class ViewTagTest extends TagTest {

    @Test
    @TmsLink("SOUL-1063")
    @DisplayName("Просмотр используемого тега")
    public void viewTag() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        createTag(name);
        tagList.add(name);
        Graph graph = createGraph("qa_at_graph_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase());
        addTagListToGraph(Collections.singletonList(name), graph.getName());
        Product product = createProduct("qa_at_product_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase());
        addTagListToProduct(Collections.singletonList(name), product.getName());
        TagsListPage list = new ControlPanelIndexPage().goToTagsPage();
        list.search(name);
        TagPage page = list.openTagPage(name);
        page.checkObject("Граф", graph.getTitle(), graph.getName());
        page.checkObject("Продукт", product.getTitle(), product.getName());
    }
}
