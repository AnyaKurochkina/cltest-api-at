package ui.t1.tests.engine.compute;

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
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.pages.cloudEngine.vpc.PublicIp;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.List;
import java.util.Objects;

import static ui.t1.pages.cloudEngine.compute.Disk.DiskInfo.COLUMN_NAME;
import static ui.t1.pages.cloudEngine.compute.Disk.DiskInfo.COLUMN_SYSTEM;

@Feature("Дополнительные")
@ExtendWith(BeforeAllExtension.class)
public class OtherTest extends AbstractComputeTest {

    @Test
    @TmsLink("1398371")
    @DisplayName("Cloud Compute. Создание ВМ c двумя доп дисками (auto_delete = on и off) boot_disk_auto_delete = on")
    void createVm2() {
        String name = getRandomName();
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setBootSize(5)
                .addDisk(name, 2, hddTypeFirst, true)
                .addDisk(name, 3, hddTypeSecond, false)
                .setName(name)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderId = vmPage.getOrderId();

        final List<StateServiceSteps.ShortItem> items = StateServiceSteps.getItems(project.getId());
        Assertions.assertEquals(4, items.stream().filter(e -> e.getOrderId().equals(orderId))
                .filter(e -> e.getSrcOrderId().equals(""))
                .filter(e -> e.getParent().equals(items.stream().filter(i -> i.getType().equals("instance")).findFirst().orElseThrow(
                        () -> new NotFoundException("Не найден item с type=compute")).getItemId()))
                .filter(i -> i.getType().equals("nic") || i.getType().equals("volume"))
                .count(), "Должно быть 4 item's (nic & volume)");

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);

        final List<StateServiceSteps.ShortItem> items2 = StateServiceSteps.getItems(project.getId());
        Assertions.assertTrue(items2.stream().noneMatch(e -> e.getOrderId().equals(orderId)), "Существуют item's с orderId=" + orderId);
        Assertions.assertEquals(1, items2.stream().filter(i -> Objects.nonNull(i.getName()))
                .filter(i -> i.getName().startsWith(vm.getName()))
                .filter(e -> {
                    if (!e.getOrderId().equals(e.getSrcOrderId()))
                        return false;
                    if (e.getSize() != 3)
                        return false;
                    return !Objects.nonNull(e.getParent());
                }).count(), "Должен быть один item с новим orderId, size=3 и parent=null");
        new IndexPage().goToDisks().selectDisk(name).runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    @Test
    @TmsLink("1398375")
    @DisplayName("Cloud Compute. Создание/Удаление ВМ c публичным IP")
    void createVmWidthPublicIp() {
        String ip = new IndexPage().goToPublicIps().addIp(availabilityZone);
        PublicIp ipPage = new PublicIpList().selectIp(ip).checkCreate();
        String orderIdIp = ipPage.getOrderId();
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setBootSize(2)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .setPublicIp(ip)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderIdVm = vmPage.getOrderId();

        String instanceId = StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("instance"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=instance")).getItemId();

        String nicId = StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("nic"))
                .filter(e -> e.getParent().equals(instanceId))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=nic")).getItemId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getSrcOrderId().equals(orderIdIp))
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(i -> i.getFloatingIpAddress().equals(ip))
                .filter(i -> i.getParent().equals(nicId))
                .count(), "Item publicIp не соответствует условиям или не найден");

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdIp))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item publicIp должен вернуть свой OrderId + Parent=null");

        new IndexPage().goToPublicIps().selectIp(ip).runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(project.getId()).stream().noneMatch(e -> Objects.equals(e.getFloatingIpAddress(), ip)));
    }

    @Test
    @TmsLink("1398380")
    @DisplayName("Cloud Compute. Подключить/Отключить диск со снимком к вм")
    void createSnapshotFromDetachDisk() {
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
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(vm.getName()).setSize(4L).clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        String orderIdDisk = diskPage.getOrderId();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(vm.getName()));
        new IndexPage().goToSnapshots().selectSnapshot(vm.getName())/*.checkCreate()*/;

        String volumeId = StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getType().equals("volume"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=volume")).getItemId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getSize().equals(disk.getSize()))
                .filter(e -> e.getParent().equals(volumeId))
                .filter(e -> e.getType().equals("snapshot"))
                .count(), "Item snapshot не соответствует условиям или не найден");

        new IndexPage().goToDisks().selectDisk(disk.getName());
        diskPage.runActionWithCheckCost(CompareType.EQUALS, () -> diskPage.attachComputeVolume(vm.getName(), false));

        String instanceId = StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("instance"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=instance")).getItemId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("snapshot"))
                .filter(e -> e.getParent().equals(volumeId))
                .count(), "Item snapshot не соответствует условиям или не найден");
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("volume"))
                .filter(e -> e.getParent().equals(instanceId))
                .filter(e -> e.getSrcOrderId().equals(orderIdDisk))
                .count(), "Item volume не соответствует условиям или не найден");

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .selectDisk(disk.getName())
                .runActionWithCheckCost(CompareType.LESS, diskPage::detachComputeVolume);

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item volume не соответствует условиям или не найден");

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> Objects.equals(e.getParent(), volumeId))
                .count(), "Item snapshot не соответствует условиям или не найден");

        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.LESS, diskPage::delete);
        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .count());
//        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    @Test
    @TmsLink("1398386")
    @DisplayName("Cloud Compute. Создание снимка системного диска и удаление вместе с вм")
    void createSystemSnapshot() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .setBootSize(7)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderIdVm = vmPage.getOrderId();
        Disk disk = vmPage.selectDisk(new Disk.DiskInfo().getRowByColumnValue(COLUMN_SYSTEM, "Да").getValueByColumn(COLUMN_NAME));
        String orderIdDisk = disk.getOrderId();
        disk.createSnapshot(vm.getName());
        new IndexPage().goToSnapshots().selectSnapshot(vm.getName()).checkCreate();

        String volumeId = StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getType().equals("volume"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=volume")).getItemId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("snapshot"))
                .filter(e -> e.getParent().equals(volumeId))
                .count(), "Item snapshot не соответствует условиям или не найден");

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .count());
        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> !Objects.equals(e.getSize(), vm.getBootSize()))
                .count());
    }

    @Test
    @TmsLinks({@TmsLink("1248945"), @TmsLink("1398401")})
    @DisplayName("Cloud Compute. Подключение диска из снимка на базе подключенного диска")
    void createSnapshotFromAttachDisk() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .setBootSize(8)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        Disk diskPage = vmPage.selectDisk(new Disk.DiskInfo().getRowByColumnValue(COLUMN_SYSTEM, "Да").getValueByColumn(COLUMN_NAME));
        diskPage.createSnapshot(vm.getName());
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
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    @Test
    @TmsLink("1398404")
    @DisplayName("Cloud Compute. Создание диска из образа MarketPlace")
    void creatDiskFromImage() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .setMarketPlaceImage(image)
                .setSize(5L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        String orderId = diskPage.getOrderId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> Objects.equals(i.getSize(), disk.getSize()))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & size == " + disk.getSize());

        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.ZERO, diskPage::delete);
        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .count(), "Item disk не соответствует условиям или не найден");
    }

    @Test
    @TmsLink("1398406")
    @DisplayName("Cloud Compute. Создание диска из пользовательского образа")
    void creatDiskFromUserImage() {
        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .setSize(2L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createImage(disk.getName()));
        Image imagePage = new IndexPage().goToImages().selectImage(disk.getName()).checkCreate();

        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.ZERO, diskPage::delete);
        DiskCreate newDisk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .setUserImage(disk.getName())
                .setSize(5L)
                .clickOrder();

        Disk newDiskPage = new DiskList().selectDisk(newDisk.getName()).checkCreate();
        String orderId = newDiskPage.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> Objects.equals(i.getSize(), newDisk.getSize()))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & size == " + newDisk.getSize());

        new IndexPage()
                .goToDisks()
                .selectDisk(newDisk.getName())
                .runActionWithCheckCost(CompareType.ZERO, newDiskPage::delete);

        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .count(), "Item disk не соответствует условиям или не найден");

//        new IndexPage().goToImages().selectImage(disk.getName()).runActionWithCheckCost(CompareType.ZERO, imagePage::delete);
    }
}
