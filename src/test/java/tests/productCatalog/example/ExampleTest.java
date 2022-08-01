package tests.productCatalog.example;

import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.example.createExample.CreateExampleResponse;
import httpModels.productCatalog.example.getExampleList.GetExampleListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.example.Example;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.*;
import static steps.productCatalog.ExampleSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Пример")
@DisabledIfEnv("prod")
public class ExampleTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/example/",
            "productCatalog/examples/createExample.json");

    @DisplayName("Создание Example в продуктовом каталоге")
    @TmsLink("822241")
    @Test
    public void createExample() {
        Example expectedExample = Example.builder()
                .name("create_example_test_api")
                .title("create_example_test_api")
                .description("create_example_test_api")
                .build()
                .createObject();
        Example actualExample = getExampleById(expectedExample.getId());
        assertEquals(expectedExample, actualExample);
    }

    @DisplayName("Получение Example по Id")
    @TmsLink("822383")
    @Test
    public void getExampleByIdTest() {
        Example expectedExample = Example.builder()
                .name("get_example_by_id_test_api")
                .title("title_get_example_by_id_test_api")
                .description("desc_get_example_by_id_test_api")
                .build()
                .createObject();
        Example actualExample = getExampleById(expectedExample.getId());
        assertEquals(expectedExample, actualExample);
    }

    @DisplayName("Поиск Example по имени, с использованием multiSearch")
    @TmsLink("822384")
    @Test
    public void searchByName() {
        Example createExample = Example.builder()
                .name("multisearch_example_test_api")
                .title("title_multisearch_example_test_api")
                .description("desc_multisearch_example_test_api")
                .build()
                .createObject();
        String exampleId = getExampleIdByNameWithMultiSearch(createExample.getName());
        assertAll(
                () -> assertNotNull(exampleId, String.format("Пример с именем: %s не найден", createExample.getName())),
                () -> assertEquals(createExample.getId(), exampleId, "Id примера не совпадают"));
    }

    @DisplayName("Обновление Example по Id")
    @TmsLink("822402")
    @Test
    public void updateExampleById() {
        Example example = Example.builder()
                .name("example_update_test_api")
                .title("title_update_example_test_api")
                .description("desc_update_example_test_api")
                .build()
                .createObject();
        String exampleId = example.getId();
        String updatedName = "updated_example_test_api";
        String updatedTitle = "updated_title_example_test_api";
        String updatedDesc = "updated_desc_example_test_api";
        Example updatedExample = Example.builder()
                .name(updatedName)
                .title(updatedTitle)
                .description(updatedDesc)
                .build();
        updatedExample.init();
        Example getUpdatedExample = putExampleById(exampleId, updatedExample.toJson());
        assertEquals(updatedName, getUpdatedExample.getName());
        assertEquals(updatedTitle, getUpdatedExample.getTitle());
    }

    @DisplayName("Частичное обновление Example по Id")
    @TmsLink("822409")
    @Test
    public void partialUpdateExampleById() {
        Example example = Example.builder()
                .name("example_partial_update_test_api")
                .title("title_partial_update_example_test_api")
                .description("desc_partial_update_example_test_api")
                .build()
                .createObject();
        String updatedDesc = "description is updated";
        String exampleId = example.getId();
        steps.partialUpdateObject(exampleId, new JSONObject().put("description", updatedDesc));
        GetImpl getUpdatedExample = steps.getById(exampleId, CreateExampleResponse.class);
        assertEquals(updatedDesc, getUpdatedExample.getDescription());
    }

    @Test
    @DisplayName("Загрузка example в GitLab")
    @DisabledIfEnv("ift")
    @TmsLink("975378")
    public void dumpToGitlabExample() {
        String exampleName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_export_to_git_api";
        Example example = Example.builder()
                .name(exampleName)
                .title(exampleName)
                .build()
                .createObject();
        Response response = steps.dumpToBitbucket(example.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
    }

    @Test
    @DisplayName("Выгрузка example из GitLab")
    @DisabledIfEnv("ift")
    @TmsLink("1028842")
    public void loadFromGitlabExample() {
        String exampleName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "_import_from_git_api";
        JSONObject jsonObject = Example.builder()
                .name(exampleName)
                .title(exampleName)
                .build()
                .init().toJson();
        CreateExampleResponse example = steps.createProductObject(jsonObject).extractAs(CreateExampleResponse.class);
        Response response = steps.dumpToBitbucket(example.getId());
        assertEquals("Committed to bitbucket", response.jsonPath().get("message"));
        steps.deleteByName(exampleName, GetExampleListResponse.class);
        String path = "example_" + exampleName;
        steps.loadFromBitbucket(new JSONObject().put("path", path));
        assertTrue(steps.isExists(exampleName));
        steps.deleteByName(exampleName, GetExampleListResponse.class);
        assertFalse(steps.isExists(exampleName));
    }

    @DisplayName("Удаление Example по Id")
    @TmsLink("822423")
    @Test
    public void deleteExampleById() {
        String exampleName = "example_delete_test_api";
        Example example = Example.builder()
                .name(exampleName)
                .title("title_partial_update_example_test_api")
                .description("desc_partial_update_example_test_api")
                .build()
                .createObject();
        steps.deleteById(example.getId());
    }
}
