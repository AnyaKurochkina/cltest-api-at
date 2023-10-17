package api.cloud.productCatalog.productCard;

import core.helper.Configure;
import core.helper.DataFileHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.ImportObject;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.productCard.CardItems;
import models.cloud.productCatalog.productCard.ProductCard;
import models.cloud.productCatalog.service.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.*;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ProductCardSteps.*;
import static steps.productCatalog.ProductSteps.createProduct;
import static steps.productCatalog.ServiceSteps.createService;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продуктовые карты")
@DisabledIfEnv("prod")
public class ProductCardExportImportTest {

    @Test
    @DisplayName("Экспорт/Импорт продуктовой карты card items")
    @TmsLink("SOUL-")
    public void exportImportProductCardTest() {
        String fileName = Configure.RESOURCE_PATH + "/json/productCatalog/productCard/importProductCard.json";
        Graph graph = createGraph();
        Service service = createService(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        Product product = createProduct(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        Action action = createAction();
        CardItems graphItem = createCardItem("Graph", graph.getGraphId(), "1.0.0");
        CardItems serviceItem = createCardItem("Service", service.getId(), "1.0.0");
        CardItems productItem = createCardItem("Product", product.getProductId(), "1.0.0");
        CardItems actionItem = createCardItem("Action", action.getActionId(), "1.0.0");
        ProductCard productCard = createProductCard("export_product_card_test_api", graphItem, serviceItem, productItem, actionItem);
        DataFileHelper.write(fileName, exportProductCard(productCard.getId()).toString());
        deleteProductCard(productCard.getId());
        ImportObject importObject = importProductCard(fileName);
        assertEquals(productCard.getName(), importObject.getObjectName());
        assertEquals("success", importObject.getStatus());
        assertTrue(isProductCardExists(productCard.getName()), "Product card не существует");
        deleteProductCardByName(productCard.getName());
    }
}
