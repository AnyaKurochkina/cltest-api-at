package api.cloud.productCatalog.productCard;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.cloud.productCatalog.productCard.ProductCard;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.ProductCardSteps.getProductCard;

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
}
