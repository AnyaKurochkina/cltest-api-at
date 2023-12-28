package ui.t1.pages.cloudEngine.backup;

import core.utils.Waiting;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.RadioGroup;
import ui.elements.Table;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.Disk;

import java.time.Duration;

public class Backup extends IProductT1Page<Backup> {

    public enum TypeBackup {
        INCREMENTAL("Инкрементальная"),
        FULL("Полная");
        private final String type;

        TypeBackup(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    @Step("Переход к полной копии {name}")
    public FullCopy selectFullCopy(String name) {
        Button.byElement(new CopyList().getRowByColumnValue(Column.NAME, name).getElementByColumn(Column.NAME)).click();
        return new FullCopy();
    }

    @Step("Получение имени последней копии")
    public String getLastFullCopyName() {
        return new CopyList().getFirstValueByColumn(Column.NAME);
    }

    @Override
    public void delete() {
        runActionWithParameters(BLOCK_PARAMETERS, "Удалить резервную копию", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        Waiting.find(() -> new TopInfo().getPowerStatus().equals(Disk.TopInfo.POWER_STATUS_DELETED), Duration.ofSeconds(60));
    }

    @Step("Создать резервную копию")
    public String createBackup(TypeBackup type) {
        CopyList table = new CopyList();
        int fullCopyCount = table.rowSize();
        int incrementalCopyCount = 0;
        String firstCopyName = getLastFullCopyName();
        if (table.isContainsIncrementalCopies(firstCopyName)) {
            incrementalCopyCount = CopyList.openFullCopy(firstCopyName).rowSize();
        }
        runActionWithParameters(BLOCK_PARAMETERS, "Создать резервную копию", "Подтвердить",
                () -> RadioGroup.byLabel("Тип резервной копии").select(type.toString()));
        generalInfoTab.switchTo();
        if (type == TypeBackup.FULL) {
            Assertions.assertEquals(fullCopyCount + 1, new CopyList().rowSize(), "Новый бэкап не появился в списке");
            return table.getFirstValueByColumn(Column.NAME);
        }
        Table incrementalCopyList = CopyList.openFullCopy(firstCopyName);
        Assertions.assertEquals(incrementalCopyCount + 1, incrementalCopyList.rowSize(), "Новый бэкап не появился в списке");
        return incrementalCopyList.getFirstValueByColumn(Column.NAME);
    }

    public static class CopyList extends Table {
        public CopyList() {
            super("Размер", 1);
        }

        public boolean isContainsIncrementalCopies(String name) {
            return new CopyList().getRowByColumnValue(Column.NAME, name).get().find(".childElement").exists();
        }

        public static FullCopy.IncrementalCopyList openFullCopy(String name) {
            Row row = new CopyList().getRowByColumnValue(Column.NAME, name);
            Button.byElement(row.getElementByColumnIndex(0)).click();
            return new FullCopy.IncrementalCopyList(row.get().sibling(0).$("table"));
        }
    }
}
