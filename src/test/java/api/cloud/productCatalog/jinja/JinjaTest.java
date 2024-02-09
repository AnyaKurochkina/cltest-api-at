package api.cloud.productCatalog.jinja;

import api.Tests;
import core.enums.Role;
import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.Jinja2Steps;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.Jinja2Steps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Jinja2")
@DisabledIfEnv("prod")
public class JinjaTest extends Tests {

    @DisplayName("Создание jinja в продуктовом каталоге")
    @TmsLink("660055")
    @Test
    public void createJinjaTest() {
        Jinja2Template jinja2 = Jinja2Steps.createJinja("create_jinja_test_api");
        Jinja2Template jinjaById = getJinja2ById(jinja2.getId());
        assertEquals(jinja2, jinjaById);
    }

    @DisplayName("Проверка сортировки по дате создания в шаблонах Jinja")
    @TmsLink("683716")
    @Test
    public void orderingByCreateData() {
        List<Jinja2Template> getJinja2List = orderingJinja2ByCreateData().getList();
        for (int i = 0; i < getJinja2List.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(getJinja2List.get(i).getCreateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(getJinja2List.get(i + 1).getCreateDt());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка сортировки по дате обновления в шаблонах Jinja")
    @TmsLink("742342")
    @Test
    public void orderingByUpDateData() {
        List<Jinja2Template> jinja2TemplateList = orderingJinja2ByUpDateData().getList();
        for (int i = 0; i < jinja2TemplateList.size() - 1; i++) {
            ZonedDateTime currentTime = ZonedDateTime.parse(jinja2TemplateList.get(i).getUpdateDt());
            ZonedDateTime nextTime = ZonedDateTime.parse(jinja2TemplateList.get(i + 1).getUpdateDt());
            assertTrue(currentTime.isBefore(nextTime) || currentTime.isEqual(nextTime),
                    "Даты должны быть отсортированы по возрастанию");
        }
    }

    @DisplayName("Проверка существования jinja по имени")
    @TmsLink("660073")
    @Test
    public void checkJinjaExists() {
        String jinjaName = "exist_jinja_test_api";
        Jinja2Steps.createJinja(jinjaName);
        assertTrue(isJinja2Exists(jinjaName));
        assertFalse(isJinja2Exists("not_exist_jinja_test_api"));
    }

    @DisplayName("Проверка доступа для методов с публичным ключом в шаблонах Jinja")
    @TmsLink("742344")
    @Test
    public void checkAccessWithPublicToken() {
        String jinjaName = "check_access_jinja_test_api";
        Jinja2Template jinja2 = Jinja2Steps.createJinja(jinjaName);
        getJinja2ByNameWithPublicToken(jinjaName).assertStatus(200);
        jinja2.setName("create_object_with_public_token_api");
        Jinja2Steps.createJinja(Role.PRODUCT_CATALOG_VIEWER, jinja2.toJson()).assertStatus(403);
        partialUpdateJinja2WithPublicToken(Role.PRODUCT_CATALOG_VIEWER, jinja2.getId(),
                new JSONObject().put("description", "UpdateDescription")).assertStatus(403);
        putJinja2ByIdWithPublicToken(Role.PRODUCT_CATALOG_VIEWER, jinja2.getId(), jinja2.toJson()).assertStatus(403);
        deleteJinja2WithPublicToken(Role.PRODUCT_CATALOG_VIEWER, jinja2.getId()).assertStatus(403);
    }

    @DisplayName("Получение jinja по Id")
    @TmsLink("660101")
    @Test
    public void getJinjaById() {
        String jinjaName = "get_by_id_jinja_test_api";
        Jinja2Template expectedJinja = Jinja2Steps.createJinja(jinjaName);
        Jinja2Template actualJinja = getJinja2ById(expectedJinja.getId());
        assertEquals(expectedJinja, actualJinja);
    }

    @DisplayName("Копирование jinja по Id")
    @TmsLink("660108")
    @Test
    public void copyJinjaById() {
        String jinjaName = "copy_jinja_test_api";
        Jinja2Template jinja2 = createJinja(jinjaName);
        String cloneName = jinja2.getName() + "-clone";
        copyJinja2ById(jinja2.getId());
        assertTrue(isJinja2Exists(cloneName));
        deleteJinjaByName(cloneName);
        assertFalse(isJinja2Exists(cloneName));
    }

    @DisplayName("Частичное обновление jinja по Id")
    @TmsLink("660122")
    @Test
    public void partialUpdateJinja() {
        String jinjaName = "partial_update_jinja_test_api";
        Jinja2Template jinja2 = createJinja(jinjaName);
        String expectedDescription = "UpdateDescription";
        partialUpdateJinja2(jinja2.getId(), new JSONObject()
                .put("description", expectedDescription)).assertStatus(200);
        Jinja2Template updatedJinja2 = getJinja2ById(jinja2.getId());
        String actualDescription = updatedJinja2.getDescription();
        assertEquals(expectedDescription, actualDescription);

    }

    @DisplayName("Обновление всего объекта jinja по Id")
    @TmsLink("660123")
    @Test
    public void updateJinjaById() {
        String updateName = "update_name";
        String updateTitle = "update_title";
        String updateDescription = "update_desc";
        if (isJinja2Exists(updateName)) {
            deleteJinjaByName(updateName);
        }
        Jinja2Template jinjaObject = createJinja("test_object");
        putJinja2ById(jinjaObject.getId(), Jinja2Template.builder()
                .name(updateName)
                .title(updateTitle)
                .description(updateDescription)
                .build()
                .toJson());
        Jinja2Template updatedJinja = getJinja2ById(jinjaObject.getId());
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
        String name = "delete_jinja_test_api";
        if (isJinja2Exists(name)) {
            deleteJinjaByName(name);
        }
        JSONObject jsonObject = Jinja2Template.builder()
                .name(name)
                .build()
                .toJson();
        Jinja2Template jinja2 = createJinja(jsonObject);
        deleteJinjaById(jinja2.getId());
    }

    @Test
    @Disabled
    @DisplayName("Загрузка Jinja в GitLab")
    @TmsLink("975380")
    public void dumpToGitlabJinja() {
        String jinjaName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Jinja2Template jinja = createJinja(jinjaName);
        String tag = "jinja2template_" + jinjaName;
        Response response = dumpJinja2ToBitbucket(jinja.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        assertEquals(tag, response.jsonPath().get("tag"));
    }

    @Test
    @Disabled
    @DisplayName("Выгрузка Jinja из GitLab")
    @TmsLink("1028947")
    public void loadFromGitlabJinja() {
        String jinjaName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = Jinja2Template.builder()
                .name(jinjaName)
                .title(jinjaName)
                .build()
                .toJson();
        Jinja2Template jinja = createJinja(jsonObject);
        Response response = dumpJinja2ToBitbucket(jinja.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        deleteJinjaByName(jinjaName);
        String path = "jinja2template_" + jinjaName;
        loadJinja2FromBitbucket(new JSONObject().put("path", path));
        assertTrue(isJinja2Exists(jinjaName));
        deleteJinjaByName(jinjaName);
        assertFalse(isJinja2Exists(jinjaName));
    }
}
