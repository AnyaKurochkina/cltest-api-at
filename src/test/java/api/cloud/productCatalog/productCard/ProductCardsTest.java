package api.cloud.productCatalog.productCard;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.product.Product;
import models.cloud.productCatalog.productCard.CardItems;
import models.cloud.productCatalog.productCard.ProductCard;
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
import static steps.productCatalog.ProductCardSteps.getProductCard;
import static steps.productCatalog.ProductCardSteps.isProductCardExists;
import static steps.productCatalog.ProductSteps.createProduct;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продуктовые карты")
@DisabledIfEnv("prod")
public class ProductCardsTest {

    @Test
    @DisplayName("Создание продуктовой карты без card items")
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
    public void checkProductExists() {
        String productName = "product_card_exist_test_api";
        ProductCard.builder()
                .name(productName)
                .build()
                .createObject();
        assertTrue(isProductCardExists(productName));
        assertFalse(isProductCardExists("not_exists_name"));
    }
}
