package ui.t1.tests.engine.backup;

import core.helper.TableChecker;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.*;
import ui.cloud.pages.CompareType;
import ui.elements.Table;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.backup.*;
import ui.t1.tests.engine.AbstractComputeTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Резервные копии")
@Epic("Cloud Compute")
public class BackupCrudTest extends AbstractComputeTest {

    @Test
    @Order(1)
    @TmsLink("SOUL-7614")
    @DisplayName("Cloud Backup. Резервные копии. Создать (контейнер)")
    void createBackup() {
        backupSup.run();
    }

    @Test
    @Order(2)
    @TmsLink("SOUL-7613")
    @Tag("smoke")
    @DisplayName("Cloud Backup. Резервные копии")
    void backupList() {
        BackupCreate backupCreate = backupSup.get();
        new IndexPage().goToBackups();
        new TableChecker()
                .add("", String::isEmpty)
                .add(Column.OBJECT_NAME, e -> e.equals(backupCreate.getObjectForBackup()))
                .add(Column.AVAILABILITY_ZONE, e -> e.equals(backupCreate.getAvailabilityZone()))
                .add("Тип объекта", e -> e.equals("instance"))
                .add("Суммарный размер", e -> e.length() > 3)
                .add("Дата последней копии", e -> e.length() > 4)
                .add("", String::isEmpty)
                .check(() -> new BackupsList.BackupTable().getRowByColumnValue(Column.OBJECT_NAME, backupCreate.getObjectForBackup()));
    }

    @Test
    @Order(3)
    @TmsLink("SOUL-7619")
    @DisplayName("Cloud Backup. Резервные копии. Контейнер")
    void backupPage() {
        BackupCreate backupCreate = backupSup.get();
        new IndexPage().goToBackups().selectBackup(backupCreate.getObjectForBackup());
        new TableChecker()
                .add(Column.NAME, e -> e.length() > 30)
                .add("Тип", e -> e.equals("Резервная копия"))
                .add(Column.STATUS, String::isEmpty)
                .add("Дата последней копии", e -> e.length() > 4)
                .add(Column.AVAILABILITY_ZONE, e -> e.equals(backupCreate.getAvailabilityZone()))
                .add("Суммарный объем копий", e -> e.length() > 4)
                .add("", String::isEmpty)
                .check(() -> new Table(Column.AVAILABILITY_ZONE).getRow(0));
    }

    @Test
    @Order(4)
    @TmsLinks({@TmsLink("SOUL-7616"), @TmsLink("SOUL-7621")})
    @DisplayName("Cloud Backup. Резервные копии. Создать/удалить резервную копию (Полную)")
    void createIncrementalBackup() {
        BackupCreate backupCreate = backupSup.get();
        Backup backup = new IndexPage().goToBackups().selectBackup(backupCreate.getObjectForBackup());
        String fullCopyName = backup.createBackup(Backup.TypeBackup.FULL);
        FullCopy fullCopy = backup.selectFullCopy(fullCopyName);
        fullCopy.runActionWithCheckCost(CompareType.ZERO, fullCopy::delete);
    }

    @Test
    @Order(5)
    @TmsLinks({@TmsLink("SOUL-7617"), @TmsLink("SOUL-7620")})
    @DisplayName("Cloud Backup. Резервные копии. Создать/удалить резервную копию (Инкрементальную)")
    void createFullBackup() {
        BackupCreate backupCreate = backupSup.get();
        Backup backup = new IndexPage().goToBackups().selectBackup(backupCreate.getObjectForBackup());
        String lastFullCopyName = backup.getLastFullCopyName();
        String incrementalCopyName = backup.createBackup(Backup.TypeBackup.INCREMENTAL);
        IncrementalCopy incrementalCopy = backup.selectFullCopy(lastFullCopyName).selectIncrementalCopy(incrementalCopyName);
        incrementalCopy.runActionWithCheckCost(CompareType.ZERO, incrementalCopy::delete);
    }

    @Test
    @Order(100)
    @TmsLink("SOUL-7622")
    @DisplayName("Cloud Backup. Резервные копии. Удалить последнюю полную копию")
    void deleteBackup() {
        BackupCreate backupCreate = backupSup.get();
        Backup backup = new IndexPage().goToBackups().selectBackup(backupCreate.getObjectForBackup());
        backup.runActionWithCheckCost(CompareType.ZERO, backup::delete);
        backup.getGeneralInfoTab().switchTo();
        Assertions.assertTrue(new Backup.CopyList().isEmpty(), "Таблица копий не пустая");
        new IndexPage().goToBackups();
        new BackupsList.BackupTable().asserts().checkColumnNotContainsValue("Имя объекта", backupCreate.getObjectForBackup());
    }
}
