package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.Objects;

import static core.utils.AssertUtils.assertHeaders;

@Feature("Снимки")
@Epic("Cloud Compute")
public class SnapshotTest extends AbstractComputeTest {

    @Test
    @TmsLink("1249426")
    @DisplayName("Cloud Compute. Снимки")
    void snapshotList() {
        new IndexPage().goToSnapshots();
        assertHeaders(new SnapshotList.SnapshotsTable(),"", "Имя", "Описание", "Зона доступности", "Источник", "Размер, ГБ", "");
    }

    @Test
    @TmsLink("1249427")
    @DisplayName("Подключение диска из снимка на базе подключенного диска")
    void createSnapshotFromAttachDisk() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm().setAvailabilityZone(availabilityZone).setImage(image)
                .setDeleteOnTermination(true).setName(getRandomName()).addSecurityGroups(securityGroup).setSshKey(sshKey).clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).markForDeletion(new VmEntity()).checkCreate(true);
        Disk diskPage = vmPage.selectDisk(new Disk.DiskInfo().getRowByColumnValue(Column.SYSTEM, "Да").getValueByColumn(Column.NAME));
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(vm.getName()));
        Snapshot snapshot = new IndexPage().goToSnapshots().selectSnapshot(vm.getName()).markForDeletion(new SnapshotEntity()).checkCreate(true);
        snapshot.createDisk(vm.getName());
        Disk createdDisk = new IndexPage().goToDisks().selectDisk(vm.getName()).markForDeletion(new DiskEntity()).checkCreate(true);
        String orderIdDisk = createdDisk.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getSrcOrderId().equals(orderIdDisk))
                .filter(e -> e.getParent().equals(""))
                .count(), "Item volume не соответствует условиям или не найден");

        createdDisk.runActionWithCheckCost(CompareType.EQUALS, () -> createdDisk.attachComputeVolume(vm.getName(), true));
    }

    @Test
    @TmsLinks({@TmsLink("1249428"), @TmsLink("1280487")})
    @DisplayName("Cloud Compute. Снимки. Удалить")
    void deleteSnapshot() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setSize(11L).setAvailabilityZone(availabilityZone).setName(getRandomName()).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).markForDeletion(new DiskEntity()).checkCreate(true);
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(disk.getName()));
        Snapshot snapshotPage = new IndexPage().goToSnapshots().selectSnapshot(disk.getName()).markForDeletion(new SnapshotEntity()).checkCreate(true);
        snapshotPage.switchProtectOrder(true);
        snapshotPage.delete();

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> Objects.equals(e.getSize(), disk.getSize()))
                .count(), "Item disk не соответствует условиям или не найден");
    }
}
