package tests.productCatalog.example;

import core.helper.Configure;
import httpModels.productCatalog.GetListImpl;
import httpModels.productCatalog.ItemImpl;
import httpModels.productCatalog.example.getExampleList.GetExampleListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.Example;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Примеры")
@DisabledIfEnv("prod")
public class ExampleListTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("/api/v1/example/",
            "productCatalog/examples/createExample.json", Configure.ProductCatalogURL);

    @DisplayName("Получение списка Examples")
    @TmsLink("822245")
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
    @TmsLink("822314")
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
    @TmsLink("822376")
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

    @DisplayName("Получение списка Examples по нескольким именам")
    @TmsLink("822382")
    @Test
    public void getExampleListBySeveralName() {
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
}
