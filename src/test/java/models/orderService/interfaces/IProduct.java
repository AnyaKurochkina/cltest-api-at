package models.orderService.interfaces;

import core.CacheService;
import io.qameta.allure.Step;
import steps.orderService.OrderServiceSteps;

public interface IProduct {
    CacheService cacheService = new CacheService();

    public String getOrderId();

    public String getProjectId();

    @Step("Заказ")
    public void order();

    @Step("Перезагрузка")
    default void reset() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("reset_vm", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }
    @Step("Принудительное выключение")
    default void stopHard() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("stop_vm_hard", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Step("Выключение")
    default void stopSoft() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("stop_vm_soft", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Step("Включить")
    default void start() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("start_vm", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }

    @Step("Удалить")
    default void delete() {
        OrderServiceSteps orderServiceSteps = new OrderServiceSteps();
        String actionId = orderServiceSteps.executeAction("delete_vm", this);
        orderServiceSteps.checkActionStatus("success", this, actionId);
    }


}
