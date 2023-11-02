package steps.calculator;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import models.cloud.orderService.interfaces.IProduct;
import steps.Steps;

import static core.helper.Configure.CalculatorURL;

@Log4j2
public class CalcCostSteps extends Steps {

    /**
     * @param  path путь в оргуструктуре
     * @return текущий расход в минуту. Может вернуть {@code Null}
     */
    @Step("Получение расхода для папки/проекта")
    public static Float getCostByPath(String path) {
        Float cost = new Http(CalculatorURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/orders/cost/?folder__startswith={}", path)
                .assertStatus(200)
                .jsonPath()
                .get("cost");
        log.info("Расход для папки/проекта {}: {}", path, cost);
        return cost;
    }

    @Step("Получение расхода для заказа")
    public static Float getCostByUid(String orderId, String projectId) {
        Float cost = new Http(CalculatorURL)
                    .setProjectId(projectId, Role.ORDER_SERVICE_ADMIN)
                    .get("/api/v1/projects/{}/order/{}/cost/", projectId, orderId)
                    .assertStatus(200)
                    .jsonPath()
                    .get("cost");
        log.info("Расход для заказа {}: {}", orderId, cost);
        return cost;
    }
}
