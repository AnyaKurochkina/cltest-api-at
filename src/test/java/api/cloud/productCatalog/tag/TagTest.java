package api.cloud.productCatalog.tag;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.TagSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Теги")
@DisabledIfEnv("prod")
public class TagTest extends Tests {

    @DisplayName("Создание Тега")
    @TmsLink("1695266")
    @Test
    public void createTagTest() {
        String tagName = "create_tag";
        createTagByName(tagName);
        assertEquals(tagName, getTagByName(tagName).getName());
        deleteTagByName(tagName);
    }
}
