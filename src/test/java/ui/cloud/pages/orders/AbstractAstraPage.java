package ui.cloud.pages.orders;

import io.qameta.allure.Step;
import models.cloud.orderService.interfaces.IProduct;

public abstract class AbstractAstraPage extends IProductPage {

    private static final String BLOCK_VM = "Виртуальные машины";

    public AbstractAstraPage(IProduct product) {
        super(product);
    }

    @Step("Установка ключа Астром")
    public void addKeyAstrom() {
        checkPowerStatus("Включено");
        runActionWithoutParameters(BLOCK_VM, "Установить Ключ-Астром");
    }

    @Step("Удаление ключа Астром")
    public void delKeyAstrom() {
        checkPowerStatus("Включено");
        runActionWithoutParameters(BLOCK_VM, "Удалить Ключ-Астром");
    }
}
