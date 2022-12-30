package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.CheckBox;
import ui.elements.Dialog;
import ui.elements.Input;
import ui.elements.Table;

import static ui.t1.pages.cloudEngine.compute.Disk.DiskInfo.COLUMN_NAME;

public class Disk extends IProductT1Page<Disk> {

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

    public void detachComputeVolume() {
        btnGeneralInfo.click();
        String name = new TopInfo().getFirstValueByColumn(COLUMN_NAME);
        runActionWithoutParameters(BLOCK_PARAMETERS, "Отключить диск от виртуальной машины");
        btnGeneralInfo.click();
        Assertions.assertFalse(new DiskInfo().isColumnValueEquals(COLUMN_NAME, name));
    }

    public void createSnapshot(String name) {
        runActionWithParameters(BLOCK_PARAMETERS, "Создать снимок", "Подтвердить", () ->
                Dialog.byTitle("Создать снимок").setInputValue("Название снимка", name));
    }

    public void createImage(String name) {
        runActionWithParameters(BLOCK_PARAMETERS, "Создать образ из диска", "Подтвердить", () ->
                Dialog.byTitle("Создать образ из диска").setInputValue("Имя образа", name));
    }

    public Snapshot selectSnapshot(String snapshot){
        getTableByHeader("Снимки").getRowByColumnValue("Имя", snapshot).get().shouldBe(Condition.visible).click();
        return new Snapshot();
    }

    public static class DiskInfo extends Table {
        public static final String COLUMN_SYSTEM = "Системный";
        public static final String COLUMN_NAME = "Имя";

        public DiskInfo() {
            super(COLUMN_SYSTEM);
        }
    }
}
