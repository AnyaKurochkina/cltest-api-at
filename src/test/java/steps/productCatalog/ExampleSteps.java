package steps.productCatalog;

import core.helper.http.Http;
import io.qameta.allure.Step;
import models.productCatalog.example.Example;
import steps.Steps;

import static core.helper.Configure.ProductCatalogURL;

public class ExampleSteps extends Steps {

    private static final String endPoint = "/api/v1/example/";

    @Step("Получение Примера продуктового каталога по Id")
    public static Example getExampleById(String exampleId) {
        return new Http(ProductCatalogURL)
                .get(endPoint + exampleId + "/")
                .extractAs(Example.class);
    }
}
