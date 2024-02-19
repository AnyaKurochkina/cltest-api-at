package steps.calculator;

import core.enums.Role;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import steps.Steps;

import static core.helper.Configure.calculatorURL;

@Log4j2
public class CalcCostSteps extends Steps {

    /**
     * @param  path путь в оргуструктуре
     * @return текущий расход в минуту. Может вернуть {@code Null}
     */
    @Step("Получение расхода для папки/проекта")
    public static Float getCostByPath(String path) {
        Float cost = new Http(calculatorURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/orders/cost/?folder__startswith={}", path)
                .assertStatus(200)
                .jsonPath()
                .getFloat("cost");
        log.info("Расход для папки/проекта {}: {}", path, cost);
        return cost;
    }

    public static Float returnFloat(Object num) {
        if (num instanceof Float) {
            return (Float) num;
        } else if (num instanceof Integer) {
            return ((Integer) num).floatValue();
        } else {
            return null;
        }
    }

    @Step("Получение расхода для заказа")
    public static Float getCostByUid(String orderId, String projectId) {
        Object cost = new Http(calculatorURL)
                .setProjectId(projectId, Role.ORDER_SERVICE_ADMIN)
                .get("/api/v1/projects/{}/order/{}/cost/", projectId, orderId)
                .assertStatus(200)
                .jsonPath()
                .get("cost");
        log.info("Расход для заказа {}: {}", orderId, cost);
        return returnFloat(cost);
    }
}
