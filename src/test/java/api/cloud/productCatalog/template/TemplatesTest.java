package api.cloud.productCatalog.template;

import api.Tests;
import core.helper.Configure;
import core.helper.JsonHelper;
import core.helper.http.Response;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.template.getListTemplate.response.GetTemplateListResponse;
import httpModels.productCatalog.template.getTemplate.response.GetTemplateResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import models.cloud.productCatalog.ErrorMessage;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.graph.GraphItem;
import models.cloud.productCatalog.icon.Icon;
import models.cloud.productCatalog.icon.IconStorage;
import models.cloud.productCatalog.template.Template;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import steps.productCatalog.ProductCatalogSteps;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.GraphSteps.partialUpdateGraph;
import static steps.productCatalog.TemplateSteps.getTemplateById;

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
                .name(templateName)
                .build()
                .createObject();
        Template getTemplate = getTemplateById(template.getId());
        assertEquals(template, getTemplate);
    }

    @DisplayName("Создание шаблона в продуктовом каталоге с иконкой")
    @TmsLink("1086277")
    @Test
    public void createTemplateWithIcon() {
        Icon icon = Icon.builder()
                .name("template_icon_for_api_test")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String templateName = "create_template_with_icon_test_api";
        Template template = Template.builder()
                .name(templateName)
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        GetTemplateResponse actualTemplate =(GetTemplateResponse) steps.getById(String.valueOf(template.getId()), GetTemplateResponse.class);
        assertFalse(actualTemplate.getIconStoreId().isEmpty());
        assertFalse(actualTemplate.getIconUrl().isEmpty());
    }

    @DisplayName("Создание нескольких шаблонов в продуктовом каталоге с одинаковой иконкой")
    @TmsLink("1086329")
    @Test
    public void createSeveralTemplateWithSameIcon() {
        Icon icon = Icon.builder()
                .name("template_icon_for_api_test2")
                .image(IconStorage.ICON_FOR_AT_TEST)
                .build()
                .createObject();
        String templateName = "create_first_template_with_same_icon_test_api";
        Template template = Template.builder()
                .name(templateName)
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();

        Template secondTemplate = Template.builder()
                .name("create_second_template_with_same_icon_test_api")
                .version("1.0.1")
                .iconStoreId(icon.getId())
                .build()
                .createObject();
        GetTemplateResponse actualFirstTemplate =(GetTemplateResponse) steps.getById(String.valueOf(template.getId()), GetTemplateResponse.class);
        GetTemplateResponse actualSecondTemplate =(GetTemplateResponse) steps.getById(String.valueOf(secondTemplate.getId()), GetTemplateResponse.class);
        assertEquals(actualFirstTemplate.getIconUrl(), actualSecondTemplate.getIconUrl());
        assertEquals(actualFirstTemplate.getIconStoreId(), actualSecondTemplate.getIconStoreId());
    }

    @DisplayName("Проверка на существование шаблона по имени")
    @TmsLink("643552")
    @Test
    public void existTemplateByName() {
        String templateName = "create_template_test_api";
        Template template = Template.builder()
                .name(templateName)
                .build()
                .createObject();
        Assertions.assertTrue(steps.isExists(template.getName()));
        Assertions.assertFalse(steps.isExists("no_exist_template"));
    }

    @DisplayName("Получение шаблона по Id")
    @TmsLink("643554")
    @Test
    public void getTemplateByIdTest() {
        String templateName = "get_by_id_template_test_api";
        Template template = Template.builder()
                .name(templateName)
                .build()
                .createObject();
        steps.getById(String.valueOf(template.getId()), GetTemplateResponse.class);
    }

    @DisplayName("Копирование шаблона по Id и удаление этого клона")
    @TmsLink("643557")
    @Test
    public void copyTemplateById() {
        String templateName = "copy_by_id_template_test_api";
        Template template = Template.builder()
                .name(templateName)
                .build()
                .createObject();
        String cloneName = template.getName() + "-clone";
        steps.copyById(String.valueOf(template.getId()));
        Assertions.assertTrue(steps.isExists(cloneName));
        steps.deleteByName(template.getName() + "-clone", GetTemplateListResponse.class);
        Assertions.assertFalse(steps.isExists(cloneName));
    }

    @DisplayName("Частичное обновление шаблона по Id")
    @TmsLink("643603")
    @Test
    public void partialUpdateTemplateById() {
        String templateName = "partial_update_template_test_api";
        Template template = Template.builder()
                .name(templateName)
                .build()
                .createObject();
        String expectedValue = "UpdateDescription";
        steps.partialUpdateObject(String.valueOf(template.getId()), new JSONObject()
                .put("description", expectedValue));
        String actual = steps.getById(String.valueOf(template.getId()), GetTemplateResponse.class)
                .getDescription();
        Assertions.assertEquals(expectedValue, actual);
    }

    @DisplayName("Импорт шаблона")
    @TmsLink("643608")
    @Test
    public void importTemplate() {
        String data = JsonHelper.getStringFromFile("/productCatalog/templates/importTemplate.json");
        String templateName = new JsonPath(data).get("Template.name");
        if(steps.isExists(templateName)) {
            steps.deleteByName(templateName, GetTemplateListResponse.class);
        }
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
                .name("template_version_test_api")
                .version("1.0.999")
                .priority(0)
                .build().createObject();
        steps.partialUpdateObject(String.valueOf(templateTest.getId()), new JSONObject().put("priority", 1));
        String currentVersion = steps.getById(String.valueOf(templateTest.getId()), GetTemplateResponse.class).getVersion();
        assertEquals("1.1.0", currentVersion);
        steps.partialUpdateObject(String.valueOf(templateTest.getId()), new JSONObject().put("priority", 2)
                .put("version", "1.999.999"));
        steps.partialUpdateObject(String.valueOf(templateTest.getId()), new JSONObject().put("priority", 3));
        currentVersion = steps.getById(String.valueOf(templateTest.getId()), GetTemplateResponse.class).getVersion();
        assertEquals("2.0.0", currentVersion);
        steps.partialUpdateObject(String.valueOf(templateTest.getId()), new JSONObject().put("priority", 4)
                .put("version", "999.999.999"));
         String errorMessage = steps.partialUpdateObject(String.valueOf(templateTest.getId()), new JSONObject().put("priority", 5))
                .assertStatus(400).extractAs(ErrorMessage.class).getMessage();
         assertEquals("Version counter full [999, 999, 999]", errorMessage);
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
                .name(templateName)
                .build()
                .createObject();
        steps.getObjectByNameWithPublicToken(templateName).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps.createJsonObject("create_object_with_public_token_api"))
                .assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(String.valueOf(template.getId()), new JSONObject()
                .put("description", "UpdateDescription")).assertStatus(403);
        steps.putObjectByIdWithPublicToken(String.valueOf(template.getId()), steps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        steps.deleteObjectWithPublicToken(String.valueOf(template.getId())).assertStatus(403);
    }

    @DisplayName("Удаление шаблона")
    @TmsLink("643616")
    @Test
    public void deleteTemplate() {
        Template template = Template.builder()
                .name("check_access_template_test_api")
                .build()
                .createObject();
        steps.deleteById(String.valueOf(template.getId()));
    }

    @Test
    @DisplayName("Загрузка Template в GitLab")
    @Disabled
    @TmsLink("975415")
    public void dumpToGitlabTemplate() {
        String templateName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Template template = Template.builder()
                .name(templateName)
                .title(templateName)
                .version("1.0.0")
                .build()
                .createObject();
        String tag = "template_" + templateName + "_" + template.getVersion();
        Response response = steps.dumpToBitbucket(String.valueOf(template.getId()));
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @Disabled
    @DisplayName("Выгрузка Template из GitLab")
    @TmsLink("1029293")
    public void loadFromGitlabTemplate() {
        String templateName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = Template.builder()
                .name(templateName)
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

    @DisplayName("Удаление шаблона используемого в узле графа")
    @TmsLink("1177502")
    @Test
    public void deleteTemplateUsedInGraphNode() {
        Template template = Template.builder()
                .name("delete_template_used_in_graph_node")
                .build()
                .createObject();
        JSONObject graphItem = GraphItem.builder()
                .name("graph_node_test_api")
                .templateId(template.getId())
                .build()
                .toJson();
        Graph graph = Graph.builder()
                .name("graph_for_delete_used_template_test_api")
                .title("graph_for_delete_used_template_test_api")
                .build()
                .createObject();
        List<JSONObject> list = new ArrayList<>();
        list.add(graphItem);
        JSONObject obj = new JSONObject().put("graph", list);
        partialUpdateGraph(graph.getGraphId(), obj);
        String errMsg = steps.getDeleteObjectResponse(Integer.toString(template.getId())).assertStatus(400)
                .extractAs(ErrorMessage.class).getMessage();
        String expectedErrorMessage = String.format("Нельзя удалить шаблон: %s. Он используется:\nGraph: (name: %s, version: %s)"
        , template.getName(), graph.getName(), "1.0.1");
        assertEquals(expectedErrorMessage, errMsg);
    }

    @DisplayName("Создание шаблона с допустимым типом")
    @TmsLink("1468913")
    @Test
    public void createTemplateWithInvalidType() {
        String templateName = "create_template_with_invalid_type_test_api";
        String expectedType = "rpc";
        Template template = Template.builder()
                .name(templateName)
                .type(expectedType)
                .build()
                .createObject();
        String actualType = getTemplateById(template.getId()).getType();
        assertEquals(expectedType, actualType);
    }
}
