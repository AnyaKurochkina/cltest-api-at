package api.cloud.productCatalog.jinja;

import core.helper.JsonHelper;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.jinja2.getJinjaListResponse.GetJinjaListResponse;
import httpModels.productCatalog.jinja2.getJinjaResponse.GetJinjaResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Jinja2;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import api.Tests;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaTest extends Tests {

    String template = "productCatalog/jinja2/createJinja.json";
    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/jinja2_templates/", template);


    @DisplayName("Создание jinja в продуктовом каталоге")
    @TmsLink("660055")
    @Test
    public void createJinja() {
        String jinjaName = "create_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        GetImpl jinjaById = steps.getById(jinja2.getJinjaId(), GetJinjaResponse.class);
        assertEquals(jinjaName, jinjaById.getName());
    }

    @DisplayName("Проверка сортировки по дате создания в шаблонах Jinja")
    @TmsLink("683716")
    @Test
    public void orderingByCreateData() {
        List<ItemImpl> list = steps.orderingByCreateData(GetJinjaListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getCreateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getCreateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в шаблонах Jinja")
    @TmsLink("742342")
    @Test
    public void orderingByUpDateData() {
        List<ItemImpl> list = steps.orderingByUpDateData(GetJinjaListResponse.class).getItemsList();
        for (int i = 0; i < list.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(list.get(i).getUpDateData());
            ZonedDateTime nextTime = ZonedDateTime.parse(list.get(i + 1).getUpDateData());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка существования jinja по имени")
    @TmsLink("660073")
    @Test
    public void checkJinjaExists() {
        String jinjaName = "exist_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        assertTrue(steps.isExists(jinja2.getName()));
        assertFalse(steps.isExists("not_exist_jinja_test_api"));
    }

    //toDO тест по импорту.

    @DisplayName("Проверка доступа для методов с публичным ключом в шаблонах Jinja")
    @TmsLink("742344")
    @Test
    public void checkAccessWithPublicToken() {
        String jinjaName = "check_access_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        steps.getObjectByNameWithPublicToken(jinjaName).assertStatus(200);
        steps.createProductObjectWithPublicToken(steps
                .createJsonObject("create_object_with_public_token_api")).assertStatus(403);
        steps.partialUpdateObjectWithPublicToken(jinja2.getJinjaId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        steps.putObjectByIdWithPublicToken(jinja2.getJinjaId(), steps
                .createJsonObject("update_object_with_public_token_api")).assertStatus(403);
        steps.deleteObjectWithPublicToken(jinja2.getJinjaId()).assertStatus(403);
    }

    @DisplayName("Получение jinja по Id")
    @TmsLink("660101")
    @Test
    public void getJinjaById() {
        String jinjaName = "get_by_id_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        GetJinjaResponse productCatalogGet = (GetJinjaResponse) steps.getById(jinja2.getJinjaId(), GetJinjaResponse.class);
        if (productCatalogGet.getError() != null) {
            fail("Ошибка: " + productCatalogGet.getError());
        } else {
            Assertions.assertEquals(jinjaName, productCatalogGet.getName());
        }
    }

    @DisplayName("Копирование jinja по Id")
    @TmsLink("660108")
    @Test
    public void copyJinjaById() {
        String jinjaName = "copy_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        String cloneName = jinja2.getName() + "-clone";
        steps.copyById(jinja2.getJinjaId());
        assertTrue(steps.isExists(cloneName));
        steps.deleteByName(cloneName, GetJinjaListResponse.class);
        assertFalse(steps.isExists(cloneName));
    }

    @DisplayName("Экспорт jinja по Id")
    @TmsLink("660113")
    @Test
    public void exportJinjaById() {
        Jinja2 jinja2 = Jinja2.builder()
                .name("copy_jinja_test_api")
                .build()
                .createObject();
        steps.exportById(jinja2.getJinjaId());
    }

    @DisplayName("Частичное обновление jinja по Id")
    @TmsLink("660122")
    @Test
    public void partialUpdateJinja() {
        String jinjaName = "partial_update_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        String expectedDescription = "UpdateDescription";
        steps.partialUpdateObject(jinja2.getJinjaId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        GetJinjaResponse getResponse = (GetJinjaResponse) steps.getById(jinja2.getJinjaId(), GetJinjaResponse.class);
        if (getResponse.getError() != null) {
            fail("Ошибка: " + getResponse.getError());
        } else {
            String actualDescription = getResponse.getDescription();
            assertEquals(expectedDescription, actualDescription);
        }
    }

    @DisplayName("Обновление всего объекта jinja по Id")
    @TmsLink("660123")
    @Test
    public void updateJinjaById() {
        String updateName = "update_name";
        String updateTitle = "update_title";
        String updateDescription = "update_desc";
        if(steps.isExists(updateName)) {
            steps.deleteByName(updateName, GetJinjaListResponse.class);
        }
        Jinja2 jinjaObject = Jinja2.builder()
                .name("test_object")
                .build()
                .createObject();
        steps.putObjectById(jinjaObject.getJinjaId(), JsonHelper.getJsonTemplate(template)
                .set("name", updateName)
                .set("title", updateTitle)
                .set("description", updateDescription)
                .build());
        GetJinjaResponse updatedJinja = (GetJinjaResponse) steps.getById(jinjaObject.getJinjaId(), GetJinjaResponse.class);
        if (updatedJinja.getError() != null) {
            fail("Ошибка: " + updatedJinja.getError());
        }
        assertAll(
                () -> assertEquals(updateName, updatedJinja.getName()),
                () -> assertEquals(updateTitle, updatedJinja.getTitle()),
                () -> assertEquals(updateDescription, updatedJinja.getDescription())
        );
    }

    @Test
    @DisplayName("Удаление jinja")
    @TmsLink("660151")
    public void deleteJinja() {
        String jinjaName = "delete_jinja_test_api";
        Jinja2 jinja2 = Jinja2.builder()
                .name(jinjaName)
                .build()
                .createObject();
        steps.deleteById(jinja2.getJinjaId());
    }

    @Test
    @DisplayName("Загрузка Jinja в GitLab")
    @TmsLink("975380")
    public void dumpToGitlabJinja() {
        String jinjaName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Jinja2 jinja = Jinja2.builder()
                .name(jinjaName)
                .title(jinjaName)
                .build()
                .createObject();
        String tag = "jinja2template_" + jinjaName;
        Response response = steps.dumpToBitbucket(jinja.getJinjaId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @DisplayName("Выгрузка Jinja из GitLab")
    @TmsLink("1028947")
    public void loadFromGitlabJinja() {
        String jinjaName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = Jinja2.builder()
                .name(jinjaName)
                .title(jinjaName)
                .build()
                .init().toJson();
        GetJinjaResponse jinja = steps.createProductObject(jsonObject).extractAs(GetJinjaResponse.class);
        Response response = steps.dumpToBitbucket(jinja.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        steps.deleteByName(jinjaName, GetJinjaListResponse.class);
        String path = "jinja2template_" + jinjaName;
        steps.loadFromBitbucket(new JSONObject().put("path", path));
        assertTrue(steps.isExists(jinjaName));
        steps.deleteByName(jinjaName, GetJinjaListResponse.class);
        assertFalse(steps.isExists(jinjaName));
    }
}
