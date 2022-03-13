package tests.productCatalog;

import core.helper.Configure;
import core.helper.JsonHelper;
import httpModels.productCatalog.jinja2.getJinjaListResponse.GetJinjaListResponse;
import httpModels.productCatalog.jinja2.getJinjaResponse.GetJinjaResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Jinja2;
import org.json.JSONObject;
import org.junit.MarkDelete;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
public class JinjaTest extends Tests {

    private static final String JINJA_NAME = "jinja_template_test_api-:2022.";
    String template = "productCatalog/jinja2/createJinja.json";
    ProductCatalogSteps productCatalogSteps = new ProductCatalogSteps("jinja2_templates/", template);
    Jinja2 jinja2;

    @Order(1)
    @DisplayName("Создание jinja в продуктовом каталоге")
    @TmsLink("660055")
    @Test
    public void createJinja() {
        jinja2 = Jinja2.builder().name(JINJA_NAME).build().createObject();
    }

    @Order(5)
    @DisplayName("Получение списка jinja")
    @TmsLink("660061")
    @Test
    public void getJinjaList() {
        assertTrue(productCatalogSteps.getProductObjectList(GetJinjaListResponse.class).size() > 0);
    }

    @Order(6)
    @DisplayName("Проверка значения next в запросе на получение списка jinja")
    @TmsLink("716386")
    @Test
    public void getMeta() {
        String str = productCatalogSteps.getMeta(GetJinjaListResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"),
                    "Значение поля next несоответсвует ожидаемому");
        }
    }

    @Order(10)
    @DisplayName("Проверка существования jinja по имени")
    @TmsLink("660073")
    @Test
    public void checkJinjaExists() {
        assertTrue(productCatalogSteps.isExists(jinja2.getName()));
        assertFalse(productCatalogSteps.isExists("NoExistsJinja"));
    }
    //toDO тест по импорту.

    @Order(15)
    @DisplayName("Получение jinja по Id")
    @TmsLink("660101")
    @Test
    public void getJinjaById() {
        GetJinjaResponse productCatalogGet = (GetJinjaResponse) productCatalogSteps.getById(jinja2.getJinjaId(), GetJinjaResponse.class);
        if (productCatalogGet.getError() != null) {
            fail("Ошибка: " + productCatalogGet.getError());
        } else {
            Assertions.assertEquals(jinja2.getName(), productCatalogGet.getName());
        }
    }

    @Order(20)
    @DisplayName("Негативный тест на получение jinja по Id без токена")
    @TmsLink("660106")
    @Test
    public void getJinjaByIdWithOutToken() {
        productCatalogSteps.getByIdWithOutToken(jinja2.getJinjaId());
    }

    @Order(25)
    @DisplayName("Копирование jinja по Id")
    @TmsLink("660108")
    @Test
    public void copyJinjaById() {
        String cloneName = jinja2.getName() + "-clone";
        productCatalogSteps.copyById(jinja2.getJinjaId());
        assertTrue(productCatalogSteps.isExists(cloneName));
        productCatalogSteps.deleteByName(cloneName, GetJinjaListResponse.class);
        assertFalse(productCatalogSteps.isExists(cloneName));
    }

    @Order(30)
    @DisplayName("Негативный тест на копирование jinja по Id без токена")
    @TmsLink("660110")
    @Test
    public void copyJinjaByIdWithOutToken() {
        productCatalogSteps.copyByIdWithOutToken(jinja2.getJinjaId());
    }

    @Order(35)
    @DisplayName("Экспорт jinja по Id")
    @TmsLink("660113")
    @Test
    public void exportJinjaById() {
        productCatalogSteps.exportById(jinja2.getJinjaId());
    }

    @Order(40)
    @DisplayName("Частичное обновление jinja по Id")
    @TmsLink("660122")
    @Test
    public void partialUpdateJinja() {
        String expectedDescription = "UpdateDescription";
        productCatalogSteps.partialUpdateObject(jinja2.getJinjaId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        GetJinjaResponse getResponse = (GetJinjaResponse) productCatalogSteps.getById(jinja2.getJinjaId(), GetJinjaResponse.class);
        if (getResponse.getError() != null) {
            fail("Ошибка: " + getResponse.getError());
        } else {
            String actualDescription = getResponse.getDescription();
            assertEquals(expectedDescription, actualDescription);
        }
    }

    @Order(45)
    @DisplayName("Обновление всего объекта jinja по Id")
    @TmsLink("660123")
    @Test
    public void updateJinjaById() {
        String updateName = "update_name";
        String updateTitle = "update_title";
        String updateDescription = "update_desc";
        Jinja2 jinjaObject = Jinja2.builder().name("test_object").build().createObject();
        productCatalogSteps.putObjectById(jinjaObject.getJinjaId(), JsonHelper.getJsonTemplate(template)
                .set("name", updateName)
                .set("title", updateTitle)
                .set("description", updateDescription)
                .build());
        GetJinjaResponse updatedJinja =(GetJinjaResponse) productCatalogSteps.getById(jinjaObject.getJinjaId(), GetJinjaResponse.class);
        if (updatedJinja.getError() != null) {
            fail("Ошибка: " + updatedJinja.getError());
        }
        assertAll(
                () -> assertEquals(updateName, updatedJinja.getName()),
                () -> assertEquals(updateTitle, updatedJinja.getTitle()),
                () -> assertEquals(updateDescription, updatedJinja.getDescription())
        );
    }

    @Order(49)
    @DisplayName("Негативный тест на обновление jinja по Id без токена")
    @TmsLink("660185")
    @Test
    public void updateJinjaByIdWithOutToken() {
        productCatalogSteps.partialUpdateObjectWithOutToken(jinja2.getJinjaId(),
                new JSONObject().put("description", "UpdateDescription"));
    }

    @Order(50)
    @DisplayName("Негативный тест на создание jinja с неуникальным именем")
    @TmsLink("660125")
    @Test
    public void createJinjaWithNonUniqueName() {
        {
            productCatalogSteps.createProductObject(productCatalogSteps
                    .createJsonObject(JINJA_NAME)).assertStatus(400);
        }
    }

    @Order(55)
    @DisplayName("Негативный тест на создание jinja с недопустимыми символами в имени")
    @TmsLink("660126")
    @Test
    public void createJinjaWithInvalidCharacters() {
        assertAll("Jinja создался с недопустимым именем",
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

    @Order(99)
    @DisplayName("Негативный тест на удаление jinja без токена")
    @TmsLink("660179")
    @Test
    public void deleteJinjaWithOutToken() {
        productCatalogSteps.deleteObjectByIdWithOutToken(jinja2.getJinjaId());
    }

    @Order(100)
    @Test
    @DisplayName("Удаление jinja")
    @TmsLink("660151")
    @MarkDelete
    public void deleteJinja() {
        try (Jinja2 jinja2 = Jinja2.builder().name(JINJA_NAME).build().createObjectExclusiveAccess()) {
            jinja2.deleteObject();
        }
    }
}
