package ui.t1.pages.cloudEngine.compute;

import io.qameta.allure.Step;
import ui.elements.Dialog;

public class Snapshot extends IProductT1Page<Snapshot> {

    public void delete() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Удалить");
    }

    @Step("Создать диск из снимка {name}")
    public void createDisk(String name) {
        runActionWithParameters(BLOCK_PARAMETERS, "Создать диск из снимка", "Подтвердить", () ->
                Dialog.byTitle("Создать диск из снимка").setInputValue("Название диска", name));
    }
}
