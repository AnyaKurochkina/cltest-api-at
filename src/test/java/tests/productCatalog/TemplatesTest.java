package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.MarkDelete;
import httpModels.productCatalog.template.existsTemplate.response.ExistsTemplateResponse;
import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
import httpModels.productCatalog.template.getTemplate.response.GetTemplateResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Template;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Продуктовый каталог")
@Feature("Шаблоны")
public class TemplatesTest extends Tests {

    Template template;
    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("templates/", "productCatalog/products/createProduct.json");

    @Order(1)
    @DisplayName("Создание шаблона в продуктовом каталоге")
    @TmsLink("643548")
    @Test
    public void createTemplate() {
        template = Template.builder()
                .templateName("template_for_at_api")
                .build()
                .createObject();
    }
    @Order(2)
    @DisplayName("Получение списка шаблонов")
    @TmsLink("643551")
    @Test
    public void getTemplateList() {
        Assertions.assertTrue(productCatalogSteps.getProductObjectList(GetTemplateListResponse.class)
                .size() > 0);
    }
    @Order(3)
    @DisplayName("Проверка на существование шаблона по имени")
    @TmsLink("643552")
    @Test
    public void existTemplateByName() {
        Assertions.assertTrue(productCatalogSteps.isExists(template.getTemplateName(), ExistsTemplateResponse.class));
        Assertions.assertFalse(productCatalogSteps.isExists("NoExistsAction", ExistsTemplateResponse.class));
    }
    @Order(4)
    @DisplayName("Получение шаблона по Id")
    @TmsLink("643554")
    @Test
    public void getTemplateById() {
        productCatalogSteps.getById(String.valueOf(template.getTemplateId()), GetTemplateResponse.class);
    }
    @Order(5)
    @DisplayName("Негативный тест на получение шаблона по Id без токена")
    @TmsLink("643556")
    @Test
    public void getTemplateByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(String.valueOf(template.getTemplateId()));
    }
    @Order(6)
    @DisplayName("Копирование шаблона по Id и удаление этого клона")
    @TmsLink("643557")
    @Test
    public void copyTemplateById() {
        String cloneName = template.getTemplateName() + "-clone";
        productCatalogSteps.copyById(String.valueOf(template.getTemplateId()));
        Assertions.assertTrue(productCatalogSteps.isExists(cloneName, ExistsTemplateResponse.class));
        productCatalogSteps.deleteByName(template.getTemplateName() + "-clone", GetTemplateListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(cloneName, ExistsTemplateResponse.class));
    }
    @Order(7)
    @DisplayName("Негатичный тест на копирование сервиса по Id без токена")
    @TmsLink("643559")
    @Test
    public void copyTemplateByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(String.valueOf(template.getTemplateId()));
    }
    @Order(8)
    @DisplayName("Обновление шаблона по Id")
    @TmsLink("643603")
    @Test
    public void updateTemplateById() {
        String expectedValue = "UpdateDescription";
        productCatalogSteps.partialUpdateObject(String.valueOf(template.getTemplateId()),
                new JSONObject().put("description", expectedValue));
        String actual = productCatalogSteps.getById(String.valueOf(template.getTemplateId()), GetTemplateResponse.class)
                .getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @Order(9)
    @DisplayName("Негативный тест на обновление шаблона по Id без токена")
    @TmsLink("643604")
    @Test
    public void updateTemplateByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(String.valueOf(template.getTemplateId()),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(10)
    @DisplayName("Негативный тест на создание шаблона с существующим именем")
    @TmsLink("643606")
    @Test
    public void createTemplateWithSameName() {
        productCatalogSteps.createProductObject(productCatalogSteps.createJsonObject(template.getTemplateName()))
                .assertStatus(400);
    }

    @Order(11)
    @DisplayName("Негативный тест на создание шаблона с недопустимыми символами в имени")
    @TmsLink("643607")
    @Test
    public void createTemplateWithInvalidCharacters() {
        assertAll("Шаблон создался с недопустимым именем",
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

    @Order(12)
    @DisplayName("Импорт шаблона")
    @TmsLink("643608")
    @Test
    public void importTemplate() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplate.json");
        String templateName = new JsonPath(data).get("Template.json.name");
        String versionArr = new JsonPath(data).get("Template.version_arr").toString();
        Assertions.assertEquals("[1, 0, 0]", versionArr);
        productCatalogSteps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/templates/importTemplate.json");
        Assertions.assertTrue(productCatalogSteps.isExists(templateName, ExistsTemplateResponse.class));
        productCatalogSteps.deleteByName(templateName, GetTemplateListResponse.class);
        Assertions.assertFalse(productCatalogSteps.isExists(templateName, ExistsTemplateResponse.class));
    }

    @Order(13)
    @DisplayName("Обновление сервиса с указанием версии в граничных значениях")
    @TmsLink("643613")
    @Test
    public void updateTemplateAndGetVersion() {
        Template templateTest = Template.builder().templateName("template_version_test_api").version("1.0.999").build().createObject();
        productCatalogSteps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api2"));
        String currentVersion = productCatalogSteps.getById(String.valueOf(templateTest.getTemplateId()), GetTemplateResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        productCatalogSteps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api3")
                .put("version", "1.999.999"));
        productCatalogSteps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api4"));
        currentVersion = productCatalogSteps.getById(String.valueOf(templateTest.getTemplateId()), GetTemplateResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        productCatalogSteps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api5")
                .put("version", "999.999.999"));
        productCatalogSteps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api6"))
                .assertStatus(500);
    }
    @Order(14)
    @DisplayName("Негативный тест на удаление шаблона без токена")
    @TmsLink("643614")
    @Test
    public void deleteTemplateWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(String.valueOf(template.getTemplateId()));
    }

    @Order(15)
    @DisplayName("Удаление шаблона")
    @TmsLink("643616")
    @MarkDelete
    @Test
    public void deleteTemplate() {
        try (Template template = Template.builder()
                .templateName("template_for_at_api")
                .build()
                .createObjectExclusiveAccess()) {
            template.deleteObject();
        }
    }
}
