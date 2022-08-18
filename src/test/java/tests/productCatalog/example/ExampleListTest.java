package tests.productCatalog.example;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.productCatalog.example.Example;
import models.productCatalog.example.GetExampleList;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import java.util.List;

import static core.helper.Configure.getAppProp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ExampleSteps.*;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Примеры")
@DisabledIfEnv("prod")
public class ExampleListTest extends Tests {

    @DisplayName("Получение списка Examples")
    @TmsLink("822245")
    @Test
    public void getExampleListTest() {
        Example.builder()
                .name("create_example_for_list_test_api")
                .title("create_example_for_list_test_api")
                .description("create_example_for_list_test_api")
                .build()
                .createObject();
        List<Example> list = getExampleList();
        assertTrue(isExampleSorted(list), "Список не отсортирован.");
    }

    @DisplayName("Проверка значения next в запросе на получение списка Examples")
    @TmsLink("822314")
    @Test
    public void getMeta() {
        String str = getExampleMeta().getNext();
        String url = getAppProp("url.kong");
        if (!(str == null)) {
            assertTrue(str.startsWith(url), "Значение поля next несоответсвует ожидаемому");
        }
    }

    @DisplayName("Получение списка Examples по имени")
    @TmsLink("822376")
    @Test
    public void getExampleListByNameTest() {
        String exampleName = "create_example_for_list_by_name_test_api";
        Example createExample = Example.builder()
                .name(exampleName)
                .title("create_example_for_list_by_name_test_api")
                .description("create_example_for_list_by_name_test_api")
                .build()
                .createObject();
        GetExampleList exampleListByName = getExampleListByName(exampleName);
        assertEquals(createExample.getName(), exampleListByName.getList().get(0).getName());
        assertEquals(1, exampleListByName.getList().size(),
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
        GetExampleList exampleListByName = getExampleListByNames(exampleName, exampleName2);
        assertEquals(2, exampleListByName.getList().size(), "Список не содержит значений");
    }
}
