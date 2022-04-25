package tests.productCatalog.example;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.Tag;
import steps.productCatalog.ProductCatalogSteps;
import tests.Tests;

@Tag("product_catalog")
@Epic("Продуктовый каталог")
@Feature("Пример")
@DisabledIfEnv("prod")
public class ExampleNegativeTest extends Tests {

    ProductCatalogSteps steps = new ProductCatalogSteps("example/",
            "productCatalog/examples/createExample.json");

}
