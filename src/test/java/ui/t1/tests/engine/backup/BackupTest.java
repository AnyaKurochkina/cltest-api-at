package ui.t1.tests.engine.backup;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import ui.cloud.pages.CompareType;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.backup.Backup;
import ui.t1.pages.cloudEngine.backup.BackupCreate;
import ui.t1.tests.engine.AbstractComputeTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Резервные копии")
@Epic("Cloud Compute")
public class BackupTest extends AbstractComputeTest {

    @Test
    @Order(1)
    @TmsLink("SOUL-8729")
    @DisplayName("Cloud Backup. Резервные копии. Восстановить из полной копии")
    void restoreFromFullBackup() {
        Backup backup = openBackup();
        String vmName = getRandomName();
        String lastFullCopyName = backup.getLastFullCopyName();
        backup.selectFullCopy(lastFullCopyName).restore(vmName, sshKey, securityGroup);
        new IndexPage().goToVirtualMachine().selectCompute(vmName)
                .markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(false);
    }

    @Test
    @Order(2)
    @TmsLink("SOUL-8728")
    @DisplayName("Cloud Backup. Резервные копии. Восстановить из инкрементальной копии")
    void restoreFromIncrementalBackup() {
        Backup backup = openBackup();
        String vmName = getRandomName();
        String lastFullCopyName = backup.getLastFullCopyName();
        String incrementalCopyName = backup.createBackup(Backup.TypeBackup.INCREMENTAL);
        backup.selectFullCopy(lastFullCopyName).selectIncrementalCopy(incrementalCopyName)
                .restore(vmName, sshKey, securityGroup);
        new IndexPage().goToVirtualMachine().selectCompute(vmName)
                .markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(false);
    }

    @Test
    @Order(100)
    @TmsLink("SOUL-7618")
    @DisplayName("Cloud Backup. Резервные копии. Удалить контейнер")
    void deleteBackup() {
        Backup backup = openBackup();
        backup.createBackup(Backup.TypeBackup.FULL);
        backup.runActionWithCheckCost(CompareType.ZERO, backup::delete);
    }

    @Step("Открытие страницы резервной копии")
    private Backup openBackup() {
        BackupCreate backupCreate = backupSup.get();
        return new IndexPage().goToBackups().selectBackup(backupCreate.getObjectForBackup());
    }
}
