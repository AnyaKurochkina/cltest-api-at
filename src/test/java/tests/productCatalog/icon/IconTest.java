package tests.productCatalog.icon;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.icon.Icon;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Иконки")
@DisabledIfEnv("prod")
public class IconTest {

    @DisplayName("Создание иконки")
    @TmsLink("")
    @Disabled
    @Test
    public void createIcon() {
        Icon icon = Icon.builder()
                .name("create_icon_test_api")
                .title("create_title_icon_test_api")
                .build()
                .createObject();
    }
}
