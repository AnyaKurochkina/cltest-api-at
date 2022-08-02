package steps.productCatalog;

import core.helper.http.Http;
import io.qameta.allure.Step;
import models.productCatalog.example.Example;
import models.productCatalog.example.GetExampleList;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.Steps;

import java.util.List;

import static core.helper.Configure.ProductCatalogURL;

public class ExampleSteps extends Steps {

    private static final String endPoint = "/api/v1/example/";

    @Step("Получение Примера продуктового каталога по Id")
    public static Example getExampleById(String exampleId) {
        return new Http(ProductCatalogURL)
                .get(endPoint + exampleId + "/")
                .extractAs(Example.class);
    }

    @Step("Поиск ID Примера продуктового каталога по имени с использованием multiSearch")
    public static String getExampleIdByNameWithMultiSearch(String name) {
        String objectId = null;
        List<Example> list = new Http(ProductCatalogURL)
                .get(endPoint + "?include=total_count&page=1&per_page=50&multisearch=" + name)
                .assertStatus(200).extractAs(GetExampleList.class).getList();
        for (Example example : list) {
            if (example.getName().equals(name)) {
                objectId = example.getId();
                break;
            }
        }
        Assertions.assertNotNull(objectId, String.format("Объект с именем: %s, с помощью multiSearch не найден", name));
        return objectId;
    }

    @Step("Обновление всего объекта продуктового каталога по Id")
    public static Example putExampleById(String objectId, JSONObject body) {
        return new Http(ProductCatalogURL)
                .body(body)
                .put(endPoint + objectId + "/")
                .assertStatus(200).extractAs(Example.class);
    }
}
