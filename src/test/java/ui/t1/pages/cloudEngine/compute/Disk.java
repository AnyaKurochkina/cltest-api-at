package ui.t1.pages.cloudEngine.compute;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.Dialog;
import ui.elements.Input;
import ui.elements.Select;
import ui.elements.Table;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

public class Disk extends IProductT1Page<Disk> {

    @Step("Подключить диск {vmName} с автоудалением = {deleteOnTermination}")
    public void attachComputeVolume(String vmName, boolean deleteOnTermination) {
        runActionWithParameters(BLOCK_PARAMETERS, "Подключить к серверу", "Подтвердить", () ->
                Dialog.byTitle("Подключить к серверу")
                        .setSelectValue("Доступные серверы", vmName + ":"));
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
    public Snapshot selectSnapshot(String name) {
        getTableByHeader("Снимки").getRowByColumnValue(Column.NAME, name).get().shouldBe(Condition.visible).click();
        return new Snapshot();
    }

    @Step("Изменить тип диска на {type}")
    public void changeTypeDisk(String type) {
        runActionWithParameters(BLOCK_PARAMETERS, "Изменить тип диска", "Подтвердить", () -> {
            Dialog.byTitle("Изменить тип диска");
            Select.byLabel("Тип").setContains(type);
        }, ActionParameters.builder().timeout(Duration.ofMinutes(2)).build());
    }

    public static class DiskInfo extends Table {
        public DiskInfo() {
            super(Column.SYSTEM);
        }
    }
}
