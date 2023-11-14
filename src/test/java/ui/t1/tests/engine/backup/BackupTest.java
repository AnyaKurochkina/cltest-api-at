package ui.t1.tests.engine.backup;

import core.helper.TableChecker;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.backup.BackupCreate;
import ui.t1.pages.cloudEngine.backup.BackupsList;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Резервные копии")
@Epic("Cloud Compute")
public class BackupTest extends AbstractComputeTest {

    private final EntitySupplier<BackupCreate> backupSup = lazy(() -> {
        VmCreate vm = randomVm.get();
        BackupCreate backupCreate = new IndexPage().goToBackups().addBackup().setAvailabilityZone(availabilityZone).setSourceType("Сервер")
                .setObjectForBackup(vm.getName()).clickOrder();
        new BackupsList().selectBackup(backupCreate.getObjectForBackup())
                .markForDeletion(new VolumeEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        return backupCreate;
    });

    @Test
    @Order(1)
    @TmsLink("")
    @DisplayName("Cloud Backup. Резервные копии. Создать резервную копию")
    void create() {
        backupSup.run();
    }

    @Test
    @Order(2)
    @TmsLink("")
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
}
