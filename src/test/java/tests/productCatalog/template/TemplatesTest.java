package tests.productCatalog.template;

import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
import httpModels.productCatalog.template.getTemplate.response.GetTemplateResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.productCatalog.Template;
import models.productCatalog.icon.IconStorage;
import org.apache.commons.lang.RandomStringUtils;
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

import static org.junit.jupiter.api.Assertions.*;

@Epic("Продуктовый каталог")
@Feature("Шаблоны")
@Tag("product_catalog")
@DisabledIfEnv("prod")
public class TemplatesTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/templates/",
            "productCatalog/templates/createTemplate.json");

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

    @DisplayName("Создание шаблона в продуктовом каталоге с иконкой")
    @TmsLink("1086277")
    @Test
    public void createTemplateWithIcon() {
        String templateName = "create_template_with_icon_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .version("1.0.1")
                .icon(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        GetTemplateResponse actualTemplate =(GetTemplateResponse) steps.getById(String.valueOf(template.getTemplateId()), GetTemplateResponse.class);
        assertFalse(actualTemplate.getIconStoreId().isEmpty());
        assertFalse(actualTemplate.getIconUrl().isEmpty());
    }

    @DisplayName("Создание нескольких шаблонов в продуктовом каталоге с одинаковой иконкой")
    @TmsLink("1086329")
    @Test
    public void createSeveralTemplateWithSameIcon() {
        String templateName = "create_first_template_with_same_icon_test_api";
        Template template = Template.builder()
                .templateName(templateName)
                .version("1.0.1")
                .icon(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();

        Template secondTemplate = Template.builder()
                .templateName("create_second_template_with_same_icon_test_api")
                .version("1.0.1")
                .icon(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        GetTemplateResponse actualFirstTemplate =(GetTemplateResponse) steps.getById(String.valueOf(template.getTemplateId()), GetTemplateResponse.class);
        GetTemplateResponse actualSecondTemplate =(GetTemplateResponse) steps.getById(String.valueOf(secondTemplate.getTemplateId()), GetTemplateResponse.class);
        assertEquals(actualFirstTemplate.getIconUrl(), actualSecondTemplate.getIconUrl());
        assertEquals(actualFirstTemplate.getIconStoreId(), actualSecondTemplate.getIconStoreId());
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
        String templateName = new JsonPath(data).get("Template.name");
        String versionArr = new JsonPath(data).get("Template.version_arr").toString();
        Assertions.assertEquals("[1, 0, 0]", versionArr);
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/templates/importTemplate.json");
        Assertions.assertTrue(steps.isExists(templateName));
        steps.deleteByName(templateName, GetTemplateListResponse.class);
        Assertions.assertFalse(steps.isExists(templateName));
    }

    @DisplayName("Импорт шаблона c иконкой")
    @TmsLink("1086370")
    @Test
    public void importTemplateWithIcon() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplateWithIcon.json");
        String name = new JsonPath(data).get("Template.name");
        if(steps.isExists(name)) {
            steps.deleteByName(name, GetTemplateListResponse.class);
        }
        steps.importObject(Configure.RESOURCE_PATH + "/json/productCatalog/templates/importTemplateWithIcon.json");
        String id = steps.getProductObjectIdByNameWithMultiSearch(name, GetTemplateListResponse.class);
        GetTemplateResponse template =(GetTemplateResponse) steps.getById(id, GetTemplateResponse.class);
        assertFalse(template.getIconStoreId().isEmpty());
        assertFalse(template.getIconUrl().isEmpty());
        assertTrue(steps.isExists(name), "Шаблон не существует");
        steps.deleteByName(name, GetTemplateListResponse.class);
        assertFalse(steps.isExists(name), "Шаблон существует");
    }

    @DisplayName("Обновление шаблона узла с указанием версии в граничных значениях")
    @TmsLink("643613")
    @Test
    public void updateTemplateAndGetVersion() {
        Template templateTest = Template.builder()
                .templateName("template_version_test_api")
                .version("1.0.999")
                .priority(0)
                .build().createObject();
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("priority", 1));
        String currentVersion = steps.getById(String.valueOf(templateTest.getTemplateId()), GetTemplateResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("priority", 2)
                .put("version", "1.999.999"));
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("priority", 3));
        currentVersion = steps.getById(String.valueOf(templateTest.getTemplateId()), GetTemplateResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("priority", 4)
                .put("version", "999.999.999"));
        steps.partialUpdateObject(String.valueOf(templateTest.getTemplateId()), new JSONObject().put("priority", 5))
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

    @Test
    @DisplayName("Загрузка Template в GitLab")
    @TmsLink("975415")
    public void dumpToGitlabTemplate() {
        String templateName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Template template = Template.builder()
                .templateName(templateName)
                .title(templateName)
                .build()
                .createObject();
        Response response = steps.dumpToBitbucket(String.valueOf(template.getTemplateId()));
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
    }

    @Test
    @DisplayName("Выгрузка Template из GitLab")
    @TmsLink("1029293")
    public void loadFromGitlabTemplate() {
        String templateName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = Template.builder()
                .templateName(templateName)
                .title(templateName)
                .version("1.0.0")
                .build()
                .init().toJson();
        GetTemplateResponse template = steps.createProductObject(jsonObject).extractAs(GetTemplateResponse.class);
        Response response = steps.dumpToBitbucket(template.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        steps.deleteByName(templateName, GetTemplateListResponse.class);
        String path = "template_" + templateName + "_" + template.getVersion();
        steps.loadFromBitbucket(new JSONObject().put("path", path));
        assertTrue(steps.isExists(templateName));
        steps.deleteByName(templateName, GetTemplateListResponse.class);
        assertFalse(steps.isExists(templateName));
    }
}
