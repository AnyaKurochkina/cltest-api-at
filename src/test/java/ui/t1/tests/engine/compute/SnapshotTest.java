package ui.t1.tests.engine.compute;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.t1.pages.cloudEngine.Column;
import ui.elements.Dialog;
import ui.elements.TypifiedElement;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.Objects;

import static core.utils.AssertUtils.AssertHeaders;
import static ui.t1.pages.IProductT1Page.BLOCK_PARAMETERS;

@ExtendWith(BeforeAllExtension.class)
@Feature("Снимки")
public class SnapshotTest extends AbstractComputeTest {

    @Test
    @TmsLink("1249426")
    @DisplayName("Cloud Compute. Снимки")
    void snapshotList() {
        new IndexPage().goToSnapshots();
        AssertHeaders(new SnapshotList.SnapshotsTable(),"", "Имя", "Описание", "Зона доступности", "Источник", "Размер, ГБ", "Дата", "");
    }

    @Test
    @TmsLink("1249427")
    @DisplayName("Подключение диска из снимка на базе подключенного диска")
    void createSnapshotFromAttachDisk() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm().setAvailabilityZone(availabilityZone).setImage(image)
                .setDeleteOnTermination(true).setName(getRandomName()).addSecurityGroups(securityGroup).setSshKey(sshKey).clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        Disk diskPage = vmPage.selectDisk(new Disk.DiskInfo().getRowByColumnValue(Column.SYSTEM, "Да").getValueByColumn(Column.NAME));
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(vm.getName()));
        Snapshot snapshot = new IndexPage().goToSnapshots().selectSnapshot(vm.getName()).checkCreate();
        snapshot.runActionWithCheckCost(CompareType.MORE, () -> snapshot.createDisk(vm.getName()));
        Disk createdDisk = new IndexPage().goToDisks().selectDisk(vm.getName()).checkCreate();
        String orderIdDisk = createdDisk.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getSrcOrderId().equals(orderIdDisk))
                .filter(e -> e.getParent().equals(""))
                .count(), "Item volume не соответствует условиям или не найден");

        createdDisk.runActionWithCheckCost(CompareType.EQUALS, () -> createdDisk.attachComputeVolume(vm.getName(), true));
//        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    @Test
    @TmsLinks({@TmsLink("1249428"), @TmsLink("1280487")})
    @DisplayName("Cloud Compute. Снимки. Удалить")
    void deleteSnapshot() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setSize(11L).setAvailabilityZone(availabilityZone).setName(getRandomName()).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(disk.getName()));
        Snapshot snapshotPage = new IndexPage().goToSnapshots().selectSnapshot(disk.getName()).checkCreate();
        snapshotPage.switchProtectOrder(true);
        try {
            snapshotPage.runActionWithParameters(BLOCK_PARAMETERS, "Удалить", "Удалить", () -> {
                Dialog dlgActions = Dialog.byTitle("Удаление");
                dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
            Alert.red("Заказ защищен от удаления");
        } finally {
            TypifiedElement.refresh();
            snapshotPage.switchProtectOrder(false);
        }
        snapshotPage.delete();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> Objects.equals(e.getSize(), disk.getSize()))
                .count(), "Item disk не соответствует условиям или не найден");

        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.LESS, diskPage::delete);
    }
}
