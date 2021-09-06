package steps.calculator;

import core.helper.Configure;
import core.helper.Http;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import steps.Steps;

@Log4j2
public class CalcCostSteps extends Steps {
    private static final String URL = Configure.getAppProp("host_kong");

    /**
     * @param  path путь в оргуструктуре
     * @return текущий расход в минуту. Может вернуть {@code Null}
     */
    @Step("Получение расхода для папки/проекта")
    public Float getCostByPath(String path) {
        Float cost = new Http(URL)
                .get("calculator/orders/cost/?folder__startswith=" + path)
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
    public Float getCostByUid(IProduct product) {
        Float cost = new Http(URL)
                    .setProjectId(product.getProjectId())
                    .get("calculator/orders/cost/?uuid__in=" + product.getOrderId())
                    .assertStatus(200)
                    .jsonPath()
                    .get("cost");
        log.info("Расход для заказа {}: {}", product.getOrderId(), cost);
        return cost;
    }
}
