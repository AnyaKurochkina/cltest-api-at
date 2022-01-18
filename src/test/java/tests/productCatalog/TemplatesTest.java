package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.Template.existsTemplate.response.ExistsTemplateResponse;
import httpModels.productCatalog.Template.getListTemplate.response.GetTemplateListResponse;
import httpModels.productCatalog.Template.getTemplate.response.GetTemplateResponse;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Template;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Продуктовый каталог: шаблоны")
public class TemplatesTest extends Tests {

    Template template;
    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps();
    String productName = "templates/";
    String templatePath = "productCatalog/products/createProduct.json";

    @Order(1)
    @DisplayName("Создание шаблона в продуктовом каталоге")
    @Test
    public void createTemplate() {
        template = Template.builder()
                .templateName("template_for_at_api")
                .build()
                .createObject();
    }

    @Order(2)
    @DisplayName("Получение списка шаблонов")
    @Test
    public void getTemplateList() {
        Assertions.assertTrue(productCatalogSteps.getProductObjectList(productName, GetTemplateListResponse.class)
                .size() > 0);
    }

    @Order(3)
    @DisplayName("Проверка на существование шаблона по имени")
    @Test
    public void existTemplateByName() {
        Assertions.assertTrue(productCatalogSteps.isExists(productName, template.getTemplateName(), ExistsTemplateResponse.class));
        Assertions.assertFalse(productCatalogSteps.isExists(productName, "NoExistsAction", ExistsTemplateResponse.class));
    }

    @Order(4)
    @DisplayName("Получение шаблона по Id")
    @Test
    public void getTemplateById() {
        productCatalogSteps.getById(productName, String.valueOf(template.getTemplateId()), GetTemplateResponse.class);
    }

    @Order(5)
    @DisplayName("Негатичный тест на получение шаблона по Id без токена")
    @Test
    public void getTemplateByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(productName, String.valueOf(template.getTemplateId()), GetTemplateResponse.class);
    }

    @Order(6)
    @DisplayName("Копирование шаблона по Id и удаление этого клона")
    @Test
    public void copyTemplateById() {
        String cloneName = template.getTemplateName() + "-clone";
        productCatalogSteps.copyById(productName, String.valueOf(template.getTemplateId()));
        Assertions.assertTrue(productCatalogSteps.isExists(productName, cloneName, ExistsTemplateResponse.class));
        productCatalogSteps.deleteByName(productName, template.getTemplateName() + "-clone", GetTemplateListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, cloneName, ExistsTemplateResponse.class));
    }

    @Order(7)
    @DisplayName("Негатичный тест на копирование сервиса по Id без токена")
    @Test
    public void copyTemplateByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(productName, String.valueOf(template.getTemplateId()));
    }

    @Order(10)
    @DisplayName("Обновление шаблона по Id")
    @Test
    public void updateTemplateById() {
        String expectedValue = "UpdateDescription";
        productCatalogSteps.partialUpdateObject(productName, String.valueOf(template.getTemplateId()),
                new JSONObject().put("description", expectedValue));
        String actual = productCatalogSteps.getById(productName, String.valueOf(template.getTemplateId()), GetTemplateResponse.class).getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @Order(11)
    @DisplayName("Негативный тест на обновление шаблона по Id без токена")
    @Test
    public void updateTemplateByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(productName, String.valueOf(template.getTemplateId()),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(12)
    @DisplayName("Негативный тест на создание действия с существующим именем")
    @Test
    public void createActionWithSameName() {
        productCatalogSteps.createProductObject(productName, productCatalogSteps
                .createJsonObject(template.getTemplateName(), templatePath)).assertStatus(400);
    }

    @Order(13)
    @DisplayName("Негативный тест на создание действия с недопустимыми символами в имени")
    @Test
    public void createTemplateWithInvalidCharacters() {
        assertAll("Шаблон создался с недопустимым именем",
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("NameWithUppercase", templatePath)).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("nameWithUppercaseInMiddle", templatePath)).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("имя", templatePath)).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("Имя", templatePath)).assertStatus(500),
                () -> productCatalogSteps.createProductObject(productName, productCatalogSteps
                        .createJsonObject("a&b&c", templatePath)).assertStatus(500)
        );
    }

    @Order(14)
    @DisplayName("Импорт шаблона")
    @Test
    public void importTemplate() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplate.json");
        String templateName = new JsonPath(data).get("Template.json.name");
        String versionArr = new JsonPath(data).get("Template.version_arr").toString();
        Assertions.assertEquals("[1, 0, 0]", versionArr);
        productCatalogSteps.importObject(productName, Configure.RESOURCE_PATH + "/json/productCatalog/templates/importTemplate.json");
        Assertions.assertTrue(productCatalogSteps.isExists(productName, templateName, ExistsTemplateResponse.class));
        productCatalogSteps.deleteByName(productName, templateName, GetTemplateListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(productName, templateName, ExistsTemplateResponse.class));
    }

    @Order(90)
    @DisplayName("Негативный тест на удаление шаблона без токена")
    @Test
    public void deleteTemplateWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(productName, String.valueOf(template.getTemplateId()));
    }

    @Order(100)
    @Test
    @DisplayName("Удаление шаблона")
    @MarkDelete
    public void deleteTemplate() {
        try (Template template = Template.builder()
                .templateName("template_for_at_api")
                .build()
                .createObjectExclusiveAccess()) {
            template.deleteObject();
        }
    }
}
