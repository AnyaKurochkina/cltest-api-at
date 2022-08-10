package tests.stateService;

import core.helper.http.Response;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.Tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static steps.stateService.StateServiceSteps.getStateServiceVersion;

@Tag("state_service")
@Epic("State Service")
@Feature("State Service api")
@DisabledIfEnv("prod")
public class StateServiceTest extends Tests {

    @Test
    @DisplayName("Получение данных версии State Service")
    @TmsLink("1080847")
    public void getStateServiceVersionTest() {
        Response resp = getStateServiceVersion();
        assertNotNull(resp.jsonPath().get("build"));
        assertNotNull(resp.jsonPath().get("date"));
        assertNotNull(resp.jsonPath().get("git_hash"));
    }
}
