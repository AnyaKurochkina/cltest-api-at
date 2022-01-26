package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.itemVisualItem.getVisualTemplate.GetVisualTemplateResponse;
import httpModels.productCatalog.itemVisualItem.getVisualTemplateList.GetVisualTemplateListResponse;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.ItemVisualTemplates;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: шаблоны визуализации")
public class VisualTemplateTest extends Tests {

    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps();
    ItemVisualTemplates visualTemplates;
    private final String productName = "item_visual_templates/";
    private final String templatePath = "productCatalog/itemVisualTemplate/createItemVisual.json";

    @Order(1)
    @DisplayName("Создание шаблона визуализации в продуктовом каталоге")
    @Test
    public void createVisualTemplate() {
        visualTemplates = ItemVisualTemplates.builder().name("item_visual_template_test_api").build().createObject();
    }

    @Order(2)
    @DisplayName("Получение списка шаблонов визуализаций")
    @Test
    public void getVisualTemplateList() {
        Assertions.assertTrue(productCatalogSteps.getProductObjectList(productName, GetVisualTemplateListResponse.class).size() > 0);
    }

    @Order(3)
    @DisplayName("Импорт шаблона визуализации")
    @Test
    public void importVisualTemplate() {
        String data = JsonHelper.getStringFromFile("productCatalog/itemVisualTemplate/visualTemplateImport.json");
        String importName = new JsonPath(data).get("ItemVisualisationTemplate.name");
        productCatalogSteps.importObject(productName, Configure.RESOURCE_PATH
                + "/json/productCatalog/itemVisualTemplate/visualTemplateImport.json");
//        Assertions.assertTrue(productCatalogSteps.isExists(productName, importName, ExistsActionResponse.class));
//        productCatalogSteps.deleteByName(productName, importName, ActionResponse.class);
//        Assertions.assertFalse(productCatalogSteps.isExists(productName, importName, ExistsActionResponse.class));
    }

    @Order(4)
    @DisplayName("Получение шаблона визуализации по Id")
    @Test
    public void getVisualTemplateById() {
        GetImpl productCatalogGet = productCatalogSteps.getById(productName, visualTemplates.getItemId(), GetVisualTemplateResponse.class);
        Assertions.assertEquals(visualTemplates.getName(), productCatalogGet.getName());
    }

    @Order(5)
    @DisplayName("Негатичный тест на получение шаблона визуализации по Id без токена")
    @Test
    public void getVisualTemplateByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(productName, visualTemplates.getItemId(), GetVisualTemplateResponse.class);
    }

    @Order(6)
    @DisplayName("Экспорт шаблона визуализации по Id")
    @Test
    public void exportVisualTemplateById() {
        productCatalogSteps.exportById(productName, visualTemplates.getItemId());
    }

    @Order(7)
    @DisplayName("Частичное обновление шаблона визуализации по Id")
    @Test
    public void partialUpdateVisualTemplate() {
        String expectedDescription = "UpdateDescription";
        productCatalogSteps.partialUpdateObject(productName, visualTemplates.getItemId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        GetImpl getGraphResponse = productCatalogSteps.getById(productName, visualTemplates.getItemId(), GetVisualTemplateResponse.class);
        String actualDescription = getGraphResponse.getDescription();
        Assertions.assertEquals(expectedDescription, actualDescription);
    }

    @Order(100)
    @Test
    @DisplayName("Удаление шаблона визуализации")
    @MarkDelete
    public void deleteAction() {
        try (ItemVisualTemplates visualTemplates = ItemVisualTemplates.builder().name("item_visual_template_test_api").build().createObjectExclusiveAccess()) {
            visualTemplates.deleteObject();
        }
    }
}
