package ui.cloud.pages.orders;

import io.qameta.allure.Step;
import models.cloud.orderService.interfaces.IProduct;

public abstract class AbstractAstraPage extends IProductPage {

    public abstract String getVirtualTableName();

    public AbstractAstraPage(IProduct product) {
        super(product);
    }

    @Step("Установка ключа Астром")
    public void addKeyAstrom() {
        checkPowerStatus("Включено");
        runActionWithoutParameters(getVirtualTableName(), "Установить Ключ-Астром");
    }

    @Step("Удаление ключа Астром")
    public void delKeyAstrom() {
        checkPowerStatus("Включено");
        runActionWithoutParameters(getVirtualTableName(), "Удалить Ключ-Астром");
    }
}
