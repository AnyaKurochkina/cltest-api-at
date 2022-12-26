package ui.t1.pages.cloudEngine.compute;

import ui.elements.Dialog;

public class Snapshot extends IProductT1Page {

    public void delete() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Удалить");
    }

    public void createDisk(String name) {
        runActionWithParameters(BLOCK_PARAMETERS, "Создать диск из снимка", "Подтвердить", () ->
                Dialog.byTitle("Создать диск из снимка").setInputValue("Название диска", name));
    }
}
