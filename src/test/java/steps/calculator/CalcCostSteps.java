package steps.calculator;

import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
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
                .get("orders/cost/?folder__startswith={}", path)
                .assertStatus(200)
                .jsonPath()
                .get("cost");
        log.info("Расход для папки/проекта {}: {}", path, cost);
        return cost;
    }

    /**
     * @param  product продукт реализующий абстрактный класс {@code IProduct}
     * @return текущий расход в минуту. Может вернуть {@code Null}
     */
    @Step("Получение расхода для заказа")
    public static Float getCostByUid(IProduct product) {
        Float cost = new Http(CalculatorURL)
                    .setProjectId(product.getProjectId())
                    .get("orders/cost/?uuid__in={}", product.getOrderId())
                    .assertStatus(200)
                    .jsonPath()
                    .get("cost");
        log.info("Расход для заказа {}: {}", product.getOrderId(), cost);
        return cost;
    }
}
