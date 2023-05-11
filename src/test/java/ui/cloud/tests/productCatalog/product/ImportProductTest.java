package ui.cloud.tests.productCatalog.product;

import core.helper.JsonHelper;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Categories;
import models.cloud.productCatalog.product.Payment;
import models.cloud.productCatalog.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.productCatalog.GraphSteps;
import ui.cloud.pages.ControlPanelIndexPage;

import static steps.productCatalog.ProductSteps.deleteProductByName;
import static steps.productCatalog.ProductSteps.isProductExists;

@Feature("Импорт из файла")
public class ImportProductTest extends ProductBaseTest {

    @Test
    @DisplayName("Импорт продукта до первого существующего объекта")
    @TmsLink("507381")
    public void importProductTest() {
        String data = JsonHelper.getStringFromFile("/productCatalog/products/importProduct.json");
        JsonPath json = new JsonPath(data);
        String name = json.getString("Product.name");
        if (isProductExists(name)) deleteProductByName(name);
        Graph graph = GraphSteps.getGraphByNameFilter(json.getString("rel_foreign_models.graph.Graph.name"));
        new ControlPanelIndexPage()
                .goToProductsListPage()
                .importProduct("src/test/resources/json/productCatalog/products/importProduct.json")
                .findAndOpenProductPage(name)
                .checkAttributes(Product.builder().name(name).title(json.getString("Product.title"))
                        .version("1.0.0")
                        .description(json.getString("Product.description"))
                        .graphId(graph.getGraphId())
                        .graphVersion(json.getString("Product.graph_version"))
                        .category(json.getString("Product.category"))
                        .categoryV2(Categories.COMPUTE)
                        .maxCount(json.getInt("Product.max_count"))
                        .author(json.getString("Product.author"))
                        .payment(Payment.PAID)
                        .inGeneralList(json.getBoolean("Product.in_general_list"))
                        .number(json.getInt("Product.number"))
                        .build());
        deleteProductByName(name);
    }
}
