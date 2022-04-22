package tests.productCatalog.jinja;

import core.helper.Configure;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.jinja2.getJinjaListResponse.GetJinjaListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaListTest extends Tests {

    String template = "productCatalog/jinja2/createJinja.json";
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/jinja2_templates/",
            template, Configure.ProductCatalogURL);

    @DisplayName("Получение списка jinja")
    @TmsLink("660061")
    @Test
    public void getJinjaList() {
        List<ItemImpl> list = steps.getProductObjectList(GetJinjaListResponse.class);
        assertTrue(steps.isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка jinja")
    @TmsLink("716386")
    @Test
    public void getMeta() {
        String str = steps.getMeta(GetJinjaListResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"),
                    "Значение поля next несоответсвует ожидаемому");
        }
    }
}
