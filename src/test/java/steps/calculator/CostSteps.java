package steps.calculator;

import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import steps.Steps;

@Log4j2
public class CostSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_kong");

    @Step("Получение расхода для папки/проекта")
    public float getConsumptionByPath(String path) {
        float consumption = new Http(URL)
                .get("calculator/orders/cost/?folder__startswith=" + path)
                .assertStatus(200)
                .jsonPath()
                .get("cost");

        log.info("Расход для папки/проекта: " + consumption);
        return consumption * 24 * 60;
    }
}
