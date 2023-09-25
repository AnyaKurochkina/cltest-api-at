package api.t1;

import api.Tests;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static core.helper.Configure.*;

@Tag("health_check")
@Epic("Health Check")
@Feature("Health Check")
public class HealthCheckTest extends Tests {

    @Test
    public void healthCheckTest() {
        Arrays.asList(StateServiceURL, ProductCatalogURL, TarifficatorURL, )
    }
}
