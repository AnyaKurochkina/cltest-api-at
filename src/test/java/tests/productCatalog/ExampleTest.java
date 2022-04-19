package tests.productCatalog;

import core.helper.Configure;
import core.helper.http.Response;
import httpModels.productCatalog.GetImpl;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.example.createExample.CreateExampleResponse;
import httpModels.productCatalog.example.getExampleList.GetExampleListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Example;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Пример")
@DisabledIfEnv("prod")
public class ExampleTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("example/",
            "productCatalog/examples/createExample.json", Configure.ProductCatalogURL);

    @DisplayName("Создание Example в продуктовом каталоге")
    @TmsLink("")
    @Test
    public void createExample() {
        String createName = "create_example_test_api";
        Example createExample = Example.builder()
                .name(createName)
                .title("create_example_test_api")
                .description("create_example_test_api")
                .build()
                .createObject();
        GetImpl getCreateExample = steps.getById(createExample.getId(), CreateExampleResponse.class);
        assertEquals(createName, getCreateExample.getName());
    }

    @DisplayName("Получение списка Examples")
    @TmsLink("")
    @Test
    public void getExampleList() {
        Example.builder()
                .name("create_example_for_list_test_api")
                .title("create_example_for_list_test_api")
                .description("create_example_for_list_test_api")
                .build()
                .createObject();
        List<ItemImpl> list = steps.getProductObjectList(GetExampleListResponse.class);
        assertTrue(steps.isSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка Examples")
    @TmsLink("")
    @Test
    public void getMeta() {
        String str = steps.getMeta(GetExampleListResponse.class).getNext();
        String env = Configure.ENV;
        if (!(str == null)) {
            assertTrue(str.startsWith("http://" + env + "-kong-service.apps.d0-oscp.corp.dev.vtb/"), "Значение поля next " +
                    "несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка Examples по имени")
    @TmsLink("")
    @Test
    public void getExampleListByName() {
        String exampleName = "create_example_for_list_by_name_test_api";
        Example createExample = Example.builder()
                .name(exampleName)
                .title("create_example_for_list_by_name_test_api")
                .description("create_example_for_list_by_name_test_api")
                .build()
                .createObject();
        GetListImpl exampleListByName = steps.getObjectListByName(exampleName, GetExampleListResponse.class);
        assertEquals(createExample.getName(), exampleListByName.getItemsList().get(0).getName());
        assertEquals(1, exampleListByName.getItemsList().size(),
                "Список не содержит значений");
    }

    @DisplayName("Получение списка Examples по имени")
    @TmsLink("")
    @Test
    public void getExamplesListByName() {
        String exampleName = "first_example";
        Example.builder()
                .name(exampleName)
                .title("title_first_example_for_list_by_name_test_api")
                .description("description_first_example_for_list_by_name_test_api")
                .build()
                .createObject();
        String exampleName2 = "second_example";
        Example.builder()
                .name(exampleName2)
                .title("title_second_example_for_list_by_name_test_api")
                .description("description_second_example_for_list_by_name_test_api")
                .build()
                .createObject();
        GetListImpl exampleListByName = steps.getObjectsListByNames(GetExampleListResponse.class, exampleName, exampleName2);
        assertEquals(2, exampleListByName.getItemsList().size(),
                "Список не содержит значений");
    }


    @DisplayName("Получение Example по Id")
    @TmsLink("")
    @Test
    public void getExampleById() {
        String exampleName = "get_example_by_id_test_api";
        String exampleTitle = "title_get_example_by_id_test_api";
        String exampleDescription = "desc_get_example_by_id_test_api";
        Example createExample = Example.builder()
                .name(exampleName)
                .title(exampleTitle)
                .description(exampleDescription)
                .build()
                .createObject();
        GetImpl getExample = steps.getById(createExample.getId(), CreateExampleResponse.class);
        assertAll(
                () -> assertEquals(exampleName, getExample.getName()),
                () -> assertEquals(exampleTitle, getExample.getTitle()),
                () -> assertEquals(exampleDescription, getExample.getDescription())
        );
    }

    @DisplayName("Поиск Example по имени, с использованием multiSearch")
    @TmsLink("")
    @Test
    public void searchByName() {
        Example createExample = Example.builder()
                .name("multisearch_example_test_api")
                .title("title_multisearch_example_test_api")
                .description("desc_multisearch_example_test_api")
                .build()
                .createObject();
        String exampleId = steps.getProductObjectIdByNameWithMultiSearch(createExample.getName(), GetExampleListResponse.class);
        assertAll(
                () -> assertNotNull(exampleId, String.format("Пример с именем: %s не найден", createExample.getName())),
                () -> assertEquals(createExample.getId(), exampleId, "Id примера не совпадают"));
    }

    @DisplayName("Обновление Example по Id")
    @TmsLink("")
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
        steps.putObjectById(exampleId, updatedExample.toJson());
        GetImpl getUpdatedExample = steps.getById(exampleId, CreateExampleResponse.class);
        assertEquals(updatedName, getUpdatedExample.getName());
        assertEquals(updatedTitle, getUpdatedExample.getTitle());
        assertEquals(updatedDesc, getUpdatedExample.getDescription());
    }

    @DisplayName("Частичное обновление Example по Id")
    @TmsLink("")
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

    @DisplayName("Удаление Example по Id")
    @TmsLink("")
    @Test
    public void deleteExampleById() {
        String exampleName = "example_delete_test_api";
        Response response = steps.createProductObject(steps.createJsonObject(exampleName));
        steps.deleteById(response.jsonPath().get("id"));
        GetListImpl list = steps.getObjectListByName(exampleName, GetExampleListResponse.class);
        assertTrue(list.getItemsList().isEmpty());
    }
}
