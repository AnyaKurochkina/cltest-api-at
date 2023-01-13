package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;

public class Disk extends IProductT1Page<Disk> {

    @Step("Подключить диск {vmName} с автоудалением = {deleteOnTermination}")
    public void attachComputeVolume(String vmName, boolean deleteOnTermination) {
        runActionWithParameters(BLOCK_PARAMETERS, "Подключить к виртуальной машине", "Подтвердить", () ->
                        Dialog.byTitle("Подключить к виртуальной машине")
                                .setSelectValue("Доступные виртуальные машины", vmName + ":")
                                .setCheckBox(CheckBox.byLabel("Удалять вместе с виртуальной машиной"), deleteOnTermination));
    }

    @Step("Расширить диск на {size}ГБ")
    public void expandDisk(int size) {
        runActionWithParameters(BLOCK_PARAMETERS, "Расширить диск", "Подтвердить", () -> Input.byLabel("Новый размер диска").setValue(size));
        Assertions.assertEquals(String.valueOf(size), new DiskInfo().getFirstValueByColumn("Размер, Гб"));
    }

    @Step("Отключить диск")
    public void detachComputeVolume() {
        btnGeneralInfo.click();
        String name = new TopInfo().getFirstValueByColumn(Column.NAME);
        runActionWithoutParameters(BLOCK_PARAMETERS, "Отключить диск от виртуальной машины");
        btnGeneralInfo.click();
        Assertions.assertFalse(new DiskInfo().isColumnValueEquals(Column.NAME, name));
    }

    @Step("Создать снимок с именем {name}")
    public void createSnapshot(String name) {
        runActionWithParameters(BLOCK_PARAMETERS, "Создать снимок", "Подтвердить", () ->
                Dialog.byTitle("Создать снимок").setInputValue("Название снимка", name));
    }

    @Step("Создать образ с именем {name}")
    public void createImage(String name) {
        runActionWithParameters(BLOCK_PARAMETERS, "Создать образ из диска", "Подтвердить", () ->
                Dialog.byTitle("Создать образ из диска").setInputValue("Имя образа", name),
                ActionParameters.builder().checkPreBilling(false).build());
    }

    @Step("Выбрать снимок с именем {name}")
    public Snapshot selectSnapshot(String name){
        getTableByHeader("Снимки").getRowByColumnValue(Column.NAME, name).get().shouldBe(Condition.visible).click();
        return new Snapshot();
    }

    public static class DiskInfo extends Table {
        public DiskInfo() {
            super(Column.SYSTEM);
        }
    }
}
