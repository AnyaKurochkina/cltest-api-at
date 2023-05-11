package ui.cloud.tests.productCatalog.product;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import models.cloud.productCatalog.product.Categories;
import models.cloud.productCatalog.product.OnRequest;
import models.cloud.productCatalog.product.Payment;
import models.cloud.productCatalog.product.Product;
import org.junit.jupiter.api.BeforeEach;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.pages.productCatalog.enums.graph.GraphType;
import ui.cloud.tests.productCatalog.BaseTest;

import java.util.HashMap;
import java.util.UUID;

@Epic("Конструктор.Продукты")
public class ProductBaseTest extends BaseTest {

    final static String TITLE = "AT UI Product";
    final static String GRAPH_TITLE = "AT UI Graph for product";
    final static String DESCRIPTION = "Description";
    private static final ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/products/",
            "productCatalog/products/createProduct.json");
    protected final String NAME = UUID.randomUUID().toString();
    protected final String GRAPH_NAME = UUID.randomUUID().toString();
    protected Product product;
    protected Graph graph;

    @BeforeEach
    public void setUp() {
        createProduct(NAME);
    }

    private void createProduct(String name) {
        graph = Graph.builder()
                .name(GRAPH_NAME)
                .title(GRAPH_TITLE)
                .version("1.0.0")
                .type(GraphType.CREATING.getValue())
                .author("AT UI")
                .build()
                .createObject();

        Icon icon = Icon.builder()
                .name(name)
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();

        product = Product.builder()
                .name(name)
                .title(TITLE)
                .description(DESCRIPTION)
                .info(new HashMap<String, String>() {{
                    put("url", "test");
                }})
                .version("1.0.0")
                .graphId(graph.getGraphId())
                .graphVersion("1.0.0")
                .iconStoreId(icon.getId())
                .category(Categories.VM.getValue())
                .categoryV2(Categories.COMPUTE)
                .maxCount(2)
                .onRequest(OnRequest.PREVIEW)
                .payment(Payment.PARTLY_PAID)
                .author("AT UI")
                .inGeneralList(false)
                .number(51)
                .extraData(new HashMap<String, String>() {{
                    put("average_deploy_time", "100");
                }})
                .build()
                .createObject();
    }
}
