package ui.cloud.tests.productCatalog.service;

import io.qameta.allure.Epic;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import models.cloud.productCatalog.orgDirection.OrgDirection;
import models.cloud.productCatalog.service.Service;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import steps.productCatalog.ProductCatalogSteps;
import ui.cloud.tests.productCatalog.ProductCatalogUITest;

import java.util.UUID;

import static steps.productCatalog.GraphSteps.createGraph;

@Epic("Конструктор.Сервисы")
@DisabledIfEnv("prod")
public class ServiceBaseTest extends ProductCatalogUITest {
    final static String TITLE = "AT UI Service";
    final static String GRAPH_TITLE = "AT UI Graph for service";
    final static String DESCRIPTION = "Description";
    private static final ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/services/",
            "/productCatalog/services/createServices.json");
    protected final String NAME = UUID.randomUUID().toString();
    final String GRAPH_NAME = UUID.randomUUID().toString();
    OrgDirection orgDirection;
    Service service;
    Graph graph;

    @BeforeEach
    public void setUp() {
        createService(NAME);
    }

    private void createService(String name) {
        graph = createGraph(Graph.builder()
                .name(GRAPH_NAME)
                .title(GRAPH_TITLE)
                .version("1.0.0")
                .type("service")
                .author("AT UI")
                .build());

        orgDirection = OrgDirection.builder()
                .name(UUID.randomUUID().toString())
                .title("AT UI Direction")
                .build()
                .createObject();

        Icon icon = Icon.builder()
                .name(name)
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();

        service = Service.builder()
                .directionId(orgDirection.getId())
                .name(name)
                .title(TITLE)
                .description(DESCRIPTION)
                .version("1.0.0")
                .graphId(graph.getGraphId())
                .graphVersion("1.0.0")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
    }
}
