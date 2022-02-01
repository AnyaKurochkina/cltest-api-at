package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.itemVisualItem.getVisualTemplate.GetVisualTemplateResponse;
import httpModels.productCatalog.itemVisualItem.getVisualTemplateList.GetVisualTemplateListResponse;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.ItemVisualTemplates;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: шаблоны визуализации")
public class VisualTemplateTest extends Tests {

    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("item_visual_templates/", "productCatalog/itemVisualTemplate/createItemVisual.json");
    ItemVisualTemplates visualTemplates;

    @Order(1)
    @DisplayName("Создание шаблона визуализации в продуктовом каталоге")
    @TmsLink("643631")
    @Test
    public void createVisualTemplate() {
        visualTemplates = ItemVisualTemplates.builder().name("item_visual_template_test_api")
                .eventProvider(Collections.singletonList("docker"))
                .eventType(Collections.singletonList("app"))
                .build()
                .createObject();
    }

    @Order(2)
    @DisplayName("Получение списка шаблонов визуализаций")
    @TmsLink("643632")
    @Test
    public void getVisualTemplateList() {
        assertTrue(productCatalogSteps.getProductObjectList(GetVisualTemplateListResponse.class)
                .size() > 0);
    }

    @Order(3)
    @DisplayName("Получение списка шаблонов визуализаций по фильтру event_provider")
    @TmsLink("643634")
    @Test
    public void getVisualTemplateListByProvider() {
        String providerFilter = visualTemplates.getEventProvider().get(0);
        List<ItemImpl> providerList = productCatalogSteps.getProductObjectList
                (GetVisualTemplateListResponse.class, "?event_provider=" + providerFilter);
        assertTrue(providerList.size() > 0);
        for (ItemImpl impl : providerList) {
            assertTrue(productCatalogSteps.getJsonPath(impl.getId()).getString("event_provider")
                    .contains(providerFilter));
        }
    }

    @Order(4)
    @DisplayName("Получение списка шаблонов визуализаций по фильтру event_type")
    @TmsLink("643636")
    @Test
    public void getVisualTemplateListByType() {
        String typeFilter = visualTemplates.getEventType().get(0);
        List<ItemImpl> typeList = productCatalogSteps.getProductObjectList
                (GetVisualTemplateListResponse.class, "?event_type=" + typeFilter);
        assertTrue(typeList.size() > 0);
        for (ItemImpl impl : typeList) {
            assertTrue(productCatalogSteps.getJsonPath(impl.getId()).getString("event_type").contains(typeFilter));
        }
    }

    @Order(5)
    @DisplayName("Получение списка шаблонов визуализаций по фильтрам event_provider и event_type")
    @TmsLink("643638")
    @Test
    public void getVisualTemplateListByProviderAndType() {
        String providerFilter = visualTemplates.getEventProvider().get(0);
        String typeFilter = visualTemplates.getEventType().get(0);
        List<ItemImpl> list = productCatalogSteps.getProductObjectList(GetVisualTemplateListResponse.class,
                "?event_type=" + typeFilter + "&event_provider=" + providerFilter);
        assertTrue(list.size() > 0);
        for (ItemImpl impl : list) {
            assertTrue(productCatalogSteps.getJsonPath(impl.getId()).getString("event_provider").contains(providerFilter));
            assertTrue(productCatalogSteps.getJsonPath(impl.getId()).getString("event_type").contains(typeFilter));
        }
    }

    @Order(6)
    @DisplayName("Импорт шаблона визуализации")
    @TmsLink("643640")
    @Test
    public void importVisualTemplate() {
        String data = JsonHelper.getStringFromFile("productCatalog/itemVisualTemplate/visualTemplateImport.json");
        String importName = new JsonPath(data).get("ItemVisualisationTemplate.name");
        productCatalogSteps.importObject(Configure.RESOURCE_PATH
                + "/json/productCatalog/itemVisualTemplate/visualTemplateImport.json");
        productCatalogSteps.deleteByName(importName, GetVisualTemplateListResponse.class);
    }

    @Order(7)
    @DisplayName("Получение шаблона визуализации по Id")
    @TmsLink("643644")
    @Test
    public void getVisualTemplateById() {
        GetImpl productCatalogGet = productCatalogSteps.getById(visualTemplates.getItemId(), GetVisualTemplateResponse.class);
        assertEquals(visualTemplates.getName(), productCatalogGet.getName());
    }

    @Order(8)
    @DisplayName("Негативный тест на получение шаблона визуализации по Id без токена")
    @TmsLink("643649")
    @Test
    public void getVisualTemplateByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(visualTemplates.getItemId());
    }

    @Order(9)
    @DisplayName("Экспорт шаблона визуализации по Id")
    @TmsLink("643667")
    @Test
    public void exportVisualTemplateById() {
        productCatalogSteps.exportById(visualTemplates.getItemId());
    }

    @Order(10)
    @DisplayName("Частичное обновление шаблона визуализации по Id")
    @TmsLink("643668")
    @Test
    public void partialUpdateVisualTemplate() {
        String expectedDescription = "UpdateDescription";
        productCatalogSteps.partialUpdateObject(visualTemplates.getItemId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        GetImpl getGraphResponse = productCatalogSteps.getById(visualTemplates.getItemId(), GetVisualTemplateResponse.class);
        String actualDescription = getGraphResponse.getDescription();
        assertEquals(expectedDescription, actualDescription);
    }

    @Order(11)
    @DisplayName("Получение шаблона визуализации по event_provider, event_type")
    @TmsLink("643671")
    @Test
    public void getVisualTemplateByProviderAndType() {
        GetVisualTemplateResponse visualTemplate = productCatalogSteps
                .getItemVisualTemplate(visualTemplates.getEventType().get(0), visualTemplates.getEventProvider().get(0));
        assertEquals(visualTemplate.getEventProvider(), visualTemplate.getEventProvider());
        assertEquals(visualTemplate.getEventType(), visualTemplate.getEventType());
    }

    @Order(96)
    @DisplayName("Негативный тест на создание шаблона визуализации с недопустимыми символами в имени")
    @TmsLink("643672")
    @Test
    public void createVisualTemplateWithInvalidCharacters() {
        assertAll("Шаблона визуализации создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("NameWithUppercase")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("nameWithUppercaseInMiddle")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("Имя")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("a&b&c")).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject("")).assertStatus(400),
                () -> productCatalogSteps.createProductObject(productCatalogSteps
                        .createJsonObject(" ")).assertStatus(400)
        );
    }

    @Order(100)
    @DisplayName("Удаление шаблона визуализации")
    @TmsLink("643674")
    @MarkDelete
    @Test
    public void deleteAction() {
        try (ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder().name("item_visual_template_test_api").build().createObjectExclusiveAccess()) {
            visualTemplates.deleteObject();
        }
    }
}
