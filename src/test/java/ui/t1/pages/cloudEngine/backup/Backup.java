package ui.t1.pages.cloudEngine.backup;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.Button;
import ui.elements.RadioGroup;
import ui.elements.Table;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;

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

    @Step("Создать резервную копию")
    public String createBackup(TypeBackup type) {
        CopyList table = new CopyList();
        int fullCopyCount = table.rowSize();
        int incrementalCopyCount = 0;
        String firstCopyName = table.getFirstValueByColumn(Column.NAME);
        if (table.isContainsIncrementalCopies(firstCopyName)) {
            incrementalCopyCount = CopyList.openFullCopy(firstCopyName).rowSize();
        }
        runActionWithParameters(BLOCK_PARAMETERS, "Создать резервную копию", "Подтвердить",
                () -> RadioGroup.byLabel("Тип резервной копии").select(type.toString()));
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

        public static String getFirstCopyName() {
            return new CopyList().getRow(0).getValueByColumn(Column.NAME);
        }

        public boolean isContainsIncrementalCopies(String name) {
            return new CopyList().getRowByColumnValue(Column.NAME, name).get().find(".childElement").exists();
        }

        public static FullCopy selectFullCopy(String name) {
            Button.byElement(new CopyList().getRowByColumnValue(Column.NAME, name).getElementByColumn(Column.NAME)).click();
            return new FullCopy();
        }

        public static FullCopy.IncrementalCopyList openFullCopy(String name) {
            SelenideElement elementFullCopy = new CopyList().getRowByColumnValue(Column.NAME, name).getElementByColumnIndex(0);
            Button.byElement(elementFullCopy).click();
//            Waiting.sleep(() -> elementFullCopy.sibling(1).$("table").exists(), Duration.ofSeconds(1));
            return new FullCopy.IncrementalCopyList(elementFullCopy.sibling(1).$("table"));
        }
    }
}
