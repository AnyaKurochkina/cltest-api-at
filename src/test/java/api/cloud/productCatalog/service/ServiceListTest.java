package api.cloud.productCatalog.service;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.service.Service;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;

import java.util.List;

import static core.helper.Configure.getAppProp;
import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ProductCatalogSteps.isSorted;
import static steps.productCatalog.ServiceSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Сервисы")
@DisabledIfEnv("prod")
public class ServiceListTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/services/",
            "/productCatalog/services/createServices.json");

    @DisplayName("Получение списка сервисов")
    @TmsLink("643450")
    @Test
    public void getServiceListTest() {
        List<Service> list = getServiceList();
        assertTrue(isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка сервисов")
    @TmsLink("682758")
    @Test
    public void getMeta() {
        String str = getServicesList().getMeta().getNext();
        String url = getAppProp("url.kong");
        if (!(str == null)) {
            assertTrue(str.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка сервисов по title")
    @TmsLink("738673")
    @Test
    public void getServiceListByTitle() {
        String serviceTitle = "get_service_list_by_id_title_test_api";
        Service service = Service.builder()
                .name("get_service_list_by_id_test_api")
                .title(serviceTitle)
                .description("ServiceForAT")
                .build()
                .createObject();
        List<Service> list = getServiceByFilter(format( "title={}", service.getTitle()));
        for (Service s : list) {
            assertEquals(serviceTitle, s.getTitle(), "Title не совпадают");
        }
    }

    @DisplayName("Получение списка сервисов по фильтру is_published")
    @TmsLink("811060")
    @Test
    public void getServiceListByPublished() {
        Service service = Service.builder()
                .name("service_is_published_test_api")
                .title("title_service_is_published_test_api")
                .description("service_is_published_test_api")
                .isPublished(true)
                .build()
                .createObject();
        List<Service> list = getServiceByFilter("is_published=true");
        partialUpdateServiceById(service.getId(), new JSONObject().put("is_published", false));
        for (Service s : list) {
            assertTrue(s.getIsPublished());
        }
    }
}
