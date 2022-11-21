package api.cloud.productCatalog.jinja;

import api.Tests;
import core.helper.Configure;
import httpModels.productCatalog.jinja2.getJinjaListResponse.GetJinjaListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.jinja2.Jinja2;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.Jinja2Steps.getJinja2List;
import static steps.productCatalog.ProductCatalogSteps.isSorted;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaListTest extends Tests {

    String template = "productCatalog/jinja2/createJinja.json";
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/jinja2_templates/", template);

    @DisplayName("Получение списка jinja")
    @TmsLink("660061")
    @Test
    public void getJinjaList() {
        List<Jinja2> list = getJinja2List();
        assertTrue(isSorted(list), "Список не отсортирован.");
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
