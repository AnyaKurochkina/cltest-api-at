package api.cloud.productCatalog.productCard;

import api.Tests;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.cloud.productCatalog.productCard.ProductCard;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ProductCardSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Продуктовые карты")
@DisabledIfEnv("prod")
public class ProductCardTagTest extends Tests {

    @DisplayName("Добавление/Удаление списка Тегов в продуктовых картах")
    @Test
    public void addTagProductCardTest() {
        List<String> tagList = Arrays.asList("test_card_api", "test_card_api2");
        ProductCard productCard = createProductCard();
        ProductCard productCard1 = createProductCard();
        addTagListToProductCard(tagList, productCard.getName(), productCard1.getName());
        assertEquals(tagList, getProductCard(productCard.getId()).getTagList());
        assertEquals(tagList, getProductCard(productCard1.getId()).getTagList());
        removeTagListToProductCard(tagList, productCard.getName(), productCard1.getName());
        assertTrue(getProductCard(productCard.getId()).getTagList().isEmpty());
        assertTrue(getProductCard(productCard1.getId()).getTagList().isEmpty());
    }

    @DisplayName("Проверка значения поля tag_list в продуктовых картах")
    @Test
    public void checkTagListValueTest() {
        List<String> tagList = Arrays.asList("product_card_tag_test_value", "product_card_tag_test_value2");
        ProductCard productCard = ProductCard.builder()
                .name("at_api_check_tag_list_value_product_card")
                .title("AT API Product")
                .tagList(tagList)
                .build()
                .createObject();
        ProductCard createdProductCard = getProductCard(productCard.getId());
        AssertUtils.assertEqualsList(tagList, createdProductCard.getTagList());
        tagList = Collections.singletonList("product_card_tag_test_value3");
        partialUpdateProductCard(createdProductCard.getId(), new JSONObject().put("tag_list", tagList));
        createdProductCard = getProductCard(productCard.getId());
        AssertUtils.assertEqualsList(tagList, createdProductCard.getTagList());
    }
}

