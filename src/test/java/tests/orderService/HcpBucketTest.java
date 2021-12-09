package tests.orderService;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import tests.Tests;

@Epic("Продукты")
@Feature("Nginx")
@Tags({@Tag("regress"), @Tag("orders"), @Tag("nginx"), @Tag("prod")})
public class HcpBucketTest extends Tests {
}
