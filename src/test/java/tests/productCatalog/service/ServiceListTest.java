package tests.productCatalog.service;

import core.helper.Configure;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.service.getServiceList.response.GetServiceListResponse;
import httpModels.productCatalog.service.getServiceList.response.ListItem;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Services;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void getServiceList() {
        List<ItemImpl> list = steps.getProductObjectList(GetServiceListResponse.class);
        assertTrue(steps.isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка сервисов")
    @TmsLink("682758")
    @Test
    public void getMeta() {
        String str = steps.getMeta(GetServiceListResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"),
                    "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка сервисов по title")
    @TmsLink("738673")
    @Test
    public void getServiceListByTitle() {
        String serviceTitle = "get_service_list_by_id_title_test_api";
        Services service = Services.builder()
                .serviceName("get_service_list_by_id_test_api")
                .title(serviceTitle)
                .description("ServiceForAT")
                .build()
                .createObject();
        GetListImpl list = steps.getObjectByTitle(service.getTitle(), GetServiceListResponse.class);
        for (ItemImpl item : list.getItemsList()) {
            ListItem listItem = (ListItem) item;
            assertEquals(serviceTitle, listItem.getTitle(), "Title не совпадают");
        }
    }

    @DisplayName("Получение списка сервисов по фильтру is_published")
    @TmsLink("811060")
    @Test
    public void getServiceListByPublished() {
        Services service = Services.builder()
                .serviceName("service_is_published_test_api")
                .title("title_service_is_published_test_api")
                .description("service_is_published_test_api")
                .isPublished(true)
                .build()
                .createObject();
        List<ItemImpl> serviceList = steps.getProductObjectList(GetServiceListResponse.class, "?is_published=true");
        steps.partialUpdateObject(service.getServiceId(), new JSONObject().put("is_published", false));
        for (ItemImpl item : serviceList) {
            ListItem listItem = (ListItem) item;
            assertTrue(listItem.getIsPublished());
        }
    }
}
