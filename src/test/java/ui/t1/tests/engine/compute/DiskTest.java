package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.NotFoundException;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.TypifiedElement;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.Objects;

import static core.utils.AssertUtils.assertHeaders;
import static ui.t1.pages.IProductT1Page.BLOCK_PARAMETERS;

@ExtendWith(BeforeAllExtension.class)
@Epic("Cloud Compute")
@Feature("Диски")
public class DiskTest extends AbstractComputeTest {

    @Test
    @TmsLink("1248951")
    @DisplayName("Cloud Compute. Диски")
    void diskList() {
        new IndexPage().goToDisks();
        assertHeaders(new DiskList.DiskTable(), "", "Имя", "Зона доступности", "Размер, ГБ", "Виртуальная машина", "Дата создания", "Тип", "Системный", "");
    }

    @Test
    @TmsLinks({@TmsLink("1249416"), @TmsLink("1249418"), @TmsLink("1249425")})
    @DisplayName("Cloud Compute. Диски. Создание/Удаление")
    void createDisk() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(getRandomName()).setSize(2L).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        String orderId = diskPage.getOrderId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> Objects.equals(i.getSize(), disk.getSize()))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & size == " + disk.getSize());
        diskPage.runActionWithCheckCost(CompareType.ZERO, diskPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(project.getId()).stream().noneMatch(e -> e.getOrderId().equals(orderId)));
    }

    @Test
    @TmsLink("1249419")
    @DisplayName("Cloud Compute. Диски. Защита от удаления")
    void protectDisk() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(getRandomName()).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.switchProtectOrder(true);
        try {
            diskPage.runActionWithParameters(BLOCK_PARAMETERS, "Удалить", "Удалить", () -> {
                Dialog dlgActions = Dialog.byTitle("Удаление");
                dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
            Alert.red("Заказ защищен от удаления");
            TypifiedElement.refresh();
        } finally {
            diskPage.switchProtectOrder(false);
        }
//        diskPage.delete();
    }

    @Test
    @TmsLink("1348216")
    @DisplayName("Cloud Compute. Диски. Расширить диск")
    void expandDisk() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(getRandomName()).setSize(2L).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.expandDisk(3));
//        diskPage.delete();
    }

    @Test
    @TmsLink("1249422")
    @DisplayName("Cloud Compute. Диски. Подключить/Отключить диск")
    void attachDisk() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderIdVm = vmPage.getOrderId();
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(getRandomName()).setSize(6L).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        String orderIdDisk = diskPage.getOrderId();
        diskPage.runActionWithCheckCost(CompareType.EQUALS, () -> diskPage.attachComputeVolume(vm.getName(), true));
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getSrcOrderId().equals(orderIdDisk))
                .filter(e -> e.getSize().equals(6L))
                .count(), "Item volume не соответствует условиям или не найден");
        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.EQUALS, diskPage::detachComputeVolume);
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getSize().equals(6L))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item volume не соответствует условиям или не найден");

//        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
//        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.LESS, diskPage::delete);
    }

    @Test
    @TmsLinks({@TmsLink("1249423"), @TmsLink("1249433")})
    @DisplayName("Cloud Compute. Диски. Создать/Удалить образ")
    void createImageFromDisk() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .setSize(2L)
                .clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.createImage(disk.getName());
        Image imagePage = new IndexPage().goToImages().selectImage(disk.getName()).checkCreate();
        String orderIdImage = imagePage.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdImage))
                .filter(e -> e.getSrcOrderId().equals(""))
                .filter(e -> e.getParent().equals(""))
                .count(), "Item image не соответствует условиям или не найден");

        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.ZERO, diskPage::delete);
        new IndexPage().goToImages().selectImage(disk.getName()).runActionWithCheckCost(CompareType.ZERO, imagePage::delete);
        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdImage))
                .count(), "Item image не соответствует условиям или не найден");
    }

    @Test
    @TmsLink("1249424")
    @DisplayName("Cloud Compute. Диски. Создать снимок")
    void createSnapshotFromDisk() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(getRandomName()).setSize(7L).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(disk.getName()));
        String orderIdDisk = diskPage.getOrderId();
        new IndexPage().goToSnapshots().selectSnapshot(disk.getName()).checkCreate();

        String volumeId = StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getType().equals("volume"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=volume")).getItemId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getType().equals("snapshot"))
                .filter(e -> e.getParent().equals(volumeId))
                .count(), "Item snapshot не соответствует условиям или не найден");

        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.ZERO, diskPage::delete);
        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> Objects.equals(e.getSize(), disk.getSize()))
                .count(), "Item snapshot не соответствует условиям или не найден");
    }
}
