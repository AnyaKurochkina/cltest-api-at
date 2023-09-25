package api.cloud.productCatalog.productCard;

import core.helper.JsonHelper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.productCard.CardItems;
import models.cloud.productCatalog.productCard.ProductCard;
import models.cloud.productCatalog.service.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ActionSteps.createAction;
import static steps.productCatalog.GraphSteps.createGraph;
import static steps.productCatalog.ProductCardSteps.*;
import static steps.productCatalog.ProductSteps.createProduct;
import static steps.productCatalog.ServiceSteps.createService;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продуктовые карты")
@DisabledIfEnv("prod")
public class ProductCardsTest {

    @Test
    @DisplayName("Создание продуктовой карты без card items")
    @TmsLink("SOUL-7347")
    public void createProductCardWithOutCardItemsTest() {
        ProductCard productCard = ProductCard.builder()
                .name("create_product_card_test_api")
                .title("create_product_card_title_test_api")
                .description("test_api")
                .number(1)
                .cardItems(Collections.emptyList())
                .build()
                .createObject();
        ProductCard getProductCard = getProductCard(productCard.getId());
        assertEquals(productCard, getProductCard);
    }

    @Test
    @DisplayName("Создание продуктовой карты с card items")
    @TmsLink("SOUL-7348")
    public void createProductCardWithCardItemsTest() {
        Action action = createAction();
        Product product = createProduct("product_for_card_items_test_api");
        CardItems actionCard = CardItems.builder().objType("Action")
                .objId(action.getActionId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();
        CardItems productCardItem = CardItems.builder().objType("Product")
                .objId(product.getProductId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();

        List<CardItems> cardItemsList = new ArrayList<>();
        cardItemsList.add(actionCard);
        cardItemsList.add(productCardItem);

        ProductCard productCard = ProductCard.builder()
                .name("create_product_card_test_api")
                .title("create_product_card_title_test_api")
                .description("test_api")
                .number(1)
                .cardItems(cardItemsList)
                .build()
                .createObject();
        ProductCard getProductCard = getProductCard(productCard.getId());
        for (CardItems items : getProductCard.getCardItems()) {
            if (items.getObjType().equals("Action")) {
                assertEquals(action.getName(), items.getObjKeys().getName());
            }
            if (items.getObjType().equals("Product")) {
                assertEquals(product.getName(), items.getObjKeys().getName());
            }
        }
        assertEquals(2, getProductCard.getCardItems().size());
    }

    @DisplayName("Проверка существования продуктовой карты по имени")
    @Test
    @TmsLink("SOUL-7349")
    public void checkProductCardExistsTest() {
        String productName = "product_card_exist_test_api";
        ProductCard.builder()
                .name(productName)
                .build()
                .createObject();
        assertTrue(isProductCardExists(productName));
        assertFalse(isProductCardExists("not_exists_name"));
    }

    @DisplayName("Обновление продуктовой карты")
    @Test
    @TmsLink("")
    public void updateProductCardTest() {
        String productName = "product_card_update_test_api";
        ProductCard productCard = ProductCard.builder()
                .name(productName)
                .build()
                .createObject();
        Graph graph = createGraph();
        CardItems productCardItem = CardItems.builder().objType("Graph")
                .objId(graph.getGraphId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();
        JSONObject json = ProductCard.builder()
                .name(productName)
                .cardItems(Collections.singletonList(productCardItem))
                .build()
                .toJson();
        ProductCard updatedProductCard = updateProductCard(productCard.getId(), json).assertStatus(200).extractAs(ProductCard.class);
        CardItems actualProductCardItem = updatedProductCard.getCardItems().get(0);
        assertEquals("Graph", actualProductCardItem.getObjType());
        assertEquals(graph.getName(), actualProductCardItem.getObjKeys().getName());
        assertEquals(productCardItem.getVersionArr(), actualProductCardItem.getVersionArr());
    }

    @SneakyThrows
    @DisplayName("Частичное обновление продуктовой карты")
    @Test
    @TmsLink("")
    public void partialUpdateProductCardTest() {
        Graph graph = createGraph();
        CardItems productCardGraph = CardItems.builder().objType("Graph")
                .objId(graph.getGraphId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();
        String productName = "product_card_partial_update_test_api";
        ProductCard productCard = ProductCard.builder()
                .name(productName)
                .cardItems(Collections.singletonList(productCardGraph))
                .build()
                .createObject();
        Service service = createService(RandomStringUtils.randomAlphabetic(6).toLowerCase() + "_test_api");
        CardItems productCardService = CardItems.builder().objType("Service")
                .objId(service.getId())
                .versionArr(Arrays.asList(1, 0, 0))
                .build();
        ProductCard updatedProductCard = partialUpdateProductCard(productCard.getId(), new JSONObject()
                .put("card_items", new JSONArray(JsonHelper.getCustomObjectMapper().writeValueAsString(Collections.singletonList(productCardService)))))
                .assertStatus(200).extractAs(ProductCard.class);
        CardItems actualProductCardItem = updatedProductCard.getCardItems().get(0);
        assertEquals("Service", actualProductCardItem.getObjType());
        assertEquals(service.getName(), actualProductCardItem.getObjKeys().getName());
        assertEquals(productCardService.getVersionArr(), actualProductCardItem.getVersionArr());
    }
}
