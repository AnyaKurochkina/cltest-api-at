package tests.productCatalog.template;

import core.helper.Configure;
import core.helper.JsonHelper;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
import httpModels.productCatalog.template.getTemplate.response.GetTemplateResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Template;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Продуктовый каталог")
@Feature("Шаблоны")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class TemplatesTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/templates/",
            "productCatalog/templates/createTemplate.json", Configure.ProductCatalogURL);

    @DisplayName("Создание шаблона в продуктовом каталоге")
    @TmsLink("643548")
    @Test
    public void createTemplate() {
        String templateName = "create_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        GetImpl getTemplate = steps.getById(String.valueOf(template.getTemplateId()), GetTemplateResponse.class);
        assertEquals(templateName, getTemplate.getName());
    }

    @DisplayName("Проверка на существование шаблона по имени")
    @TmsLink("643552")
    @Test
    public void existTemplateByName() {
        String templateName = "create_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        Assertions.assertTrue(steps.isExists(template.getTemplateName()));
        Assertions.assertFalse(steps.isExists("no_exist_template"));
    }

    @DisplayName("Получение шаблона по Id")
    @TmsLink("643554")
    @Test
    public void getTemplateById() {
        String templateName = "get_by_id_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        steps.getById(String.valueOf(template.getTemplateId()), GetTemplateResponse.class);
    }

    @DisplayName("Копирование шаблона по Id и удаление этого клона")
    @TmsLink("643557")
    @Test
    public void copyTemplateById() {
        String templateName = "copy_by_id_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        String cloneName = template.getTemplateName() + "-clone";
        steps.copyById(String.valueOf(template.getTemplateId()));
        Assertions.assertTrue(steps.isExists(cloneName));
        steps.deleteByName(template.getTemplateName() + "-clone", GetTemplateListResponse.class);
        Assertions.assertFalse(steps.isExists(cloneName));
    }

    @DisplayName("Частичное обновление шаблона по Id")
    @TmsLink("643603")
    @Test
    public void partialUpdateTemplateById() {
        String templateName = "partial_update_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        String expectedValue = "UpdateDescription";
        steps.partialUpdateObject(String.valueOf(template.getTemplateId()), new JSONObject()
                .put("description", expectedValue));
        String actual = steps.getById(String.valueOf(template.getTemplateId()), GetTemplateResponse.class)
                .getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @DisplayName("Импорт шаблона")
    @TmsLink("643608")
    @Test
    public void importTemplate() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplate.json");
        String templateName = new JsonPath(data).get("Template.json.name");
        String versionArr = new JsonPath(data).get("Template.version_arr").toString();
        Assertions.assertEquals("[1, 0, 0]", versionArr);
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/templates/importTemplate.json");
        Assertions.assertTrue(steps.isExists(templateName));
        steps.deleteByName(templateName, GetTemplateListResponse.class);
        Assertions.assertFalse(steps.isExists(templateName));
    }

    @DisplayName("Обновление шаблона узла с указанием версии в граничных значениях")
    @TmsLink("643613")
    @Test
    public void updateTemplateAndGetVersion() {
        Template templateTest = Template.builder()
                .templateName("template_version_test_api")
                .version("1.0.999")
                .build().createObject();
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api2"));
        String currentVersion = steps.getById(String.valueOf(templateTest.getTemplateId()), GetTemplateResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api3")
                .put("version", "1.999.999"));
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api4"));
        currentVersion = steps.getById(String.valueOf(templateTest.getTemplateId()), GetTemplateResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api5")
                .put("version", "999.999.999"));
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("name", "template_version_test_api6"))
                .assertStatus(500);
    }

    @DisplayName("Проверка сортировки по дате создания в шаблонах")
    @TmsLink("742475")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = steps
                .orderingByCreateData(GetTemplateListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в шаблонах")
    @TmsLink("742477")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = steps
                .orderingByUpDateData(GetTemplateListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в шаблонах")
    @TmsLink("742478")
    @Test
    public void checkAccessWithPublicToken() {
        String templateName = "check_access_template_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .build()
                .createObject();
        steps.getObjectByNameWithPublicToken(templateName).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps.createJsonObject("create_object_with_public_token_api"))
                .assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(String.valueOf(template.getTemplateId()), new JSONObject()
                .put("description", "UpdateDescription")).assertStatus(403);
        steps.putObjectByIdWithPublicToken(String.valueOf(template.getTemplateId()), steps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        steps.deleteObjectWithPublicToken(String.valueOf(template.getTemplateId())).assertStatus(403);
    }

    @DisplayName("Удаление шаблона")
    @TmsLink("643616")
    @Test
    public void deleteTemplate() {
        Template template = Template.builder()
                .templateName("check_access_template_test_api")
                .build()
                .createObject();
        steps.deleteById(String.valueOf(template.getTemplateId()));
    }
}
