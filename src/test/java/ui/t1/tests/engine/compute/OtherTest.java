package ui.t1.tests.engine.compute;

import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.AbstractEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NotFoundException;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.pages.cloudEngine.vpc.PublicIp;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Feature("Дополнительные")
@Epic("Cloud Compute")
public class OtherTest extends AbstractComputeTest {

    @Test
    @TmsLink("1398371")
    @DisplayName("Cloud Compute. Создание ВМ c двумя доп дисками (auto_delete = on и off) boot_disk_auto_delete = on")
    void createVm2() {
        String name = getRandomName();
        String extDiskNameFirst = name + "_first";
        String extDiskNameSecond = name + "_second";
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setBootSize(5)
                .addDisk(extDiskNameFirst, 2, hddTypeFirst)
                .addDisk(extDiskNameSecond, 3, hddTypeSecond)
                .setName(name)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderId = vmPage.getOrderId();

        final List<StateServiceSteps.ShortItem> items = StateServiceSteps.getItems(getProjectId());
        Assertions.assertEquals(4, items.stream().filter(e -> e.getOrderId().equals(orderId))
                .filter(e -> e.getSrcOrderId().isEmpty())
                .filter(e -> e.getParent().equals(items.stream().filter(i -> i.getType().equals("instance"))
                        .filter(i -> i.getOrderId().equals(orderId))
                        .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=compute")).getItemId()))
                .filter(i -> i.getType().equals("nic") || i.getType().equals("volume"))
                .count(), "Должно быть 4 item's (nic & volume)");

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS,
                () -> vmPage.delete(false, vmPage.getSystemDiskName(), extDiskNameFirst));

        final List<StateServiceSteps.ShortItem> items2 = StateServiceSteps.getItems(getProjectId());
        Assertions.assertTrue(items2.stream().noneMatch(e -> e.getOrderId().equals(orderId)), "Существуют item's с orderId=" + orderId);
        List<StateServiceSteps.ShortItem> list = items2.stream().filter(i -> Objects.nonNull(i.getName()))
                .filter(i -> i.getName().startsWith(vm.getName()))
                .filter(e -> {
                    if (!e.getOrderId().equals(e.getSrcOrderId()))
                        return false;
                    if (e.getSize() != 3)
                        return false;
                    return !Objects.nonNull(e.getParent());
                }).collect(Collectors.toList());

        Assertions.assertEquals(1, list.size(), "Должен быть один item с новым orderId, size=3 и parent=null");
        new InstanceEntity(getProjectId(), list.get(0).getOrderId(), true).deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @Test
    @TmsLink("1398375")
    @DisplayName("Cloud Compute. Создание/Удаление ВМ c публичным IP")
    void createVmWidthPublicIp() {
        String ip = new IndexPage().goToPublicIps().addIp(region);
        PublicIp ipPage = new PublicIpList().selectIp(ip).markForDeletion(new PublicIpEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderIdIp = ipPage.getOrderId();
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setName(getRandomName())
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .setBootSize(2)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .setPublicIp(ip)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(false);
        String orderIdVm = vmPage.getOrderId();

        String instanceId = StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("instance"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=instance")).getItemId();

        String nicId = StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("nic"))
                .filter(e -> e.getParent().equals(instanceId))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=nic")).getItemId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getSrcOrderId().equals(orderIdIp))
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(i -> i.getFloatingIpAddress().equals(ip))
                .filter(i -> i.getParent().equals(nicId))
                .count(), "Item publicIp не соответствует условиям или не найден");

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdIp))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item publicIp должен вернуть свой OrderId + Parent=null");

        new IndexPage().goToPublicIps().selectIp(ip).runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(getProjectId()).stream().noneMatch(e -> Objects.equals(e.getFloatingIpAddress(), ip)));
    }

    @Test
    @TmsLink("1398380")
    @DisplayName("Cloud Compute. Подключить/Отключить диск со снимком к вм")
    void createSnapshotFromDetachDisk() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setName(getRandomName())
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderIdVm = vmPage.getOrderId();
        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(vm.getName()).setSize(4L).clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).markForDeletion(new VolumeEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderIdDisk = diskPage.getOrderId();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(vm.getName()));
        new IndexPage().goToSnapshots().selectSnapshot(vm.getName()).markForDeletion(new SnapshotEntity(), AbstractEntity.Mode.AFTER_TEST)/*.checkCreate(true)*/;

        String volumeId = StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getType().equals("volume"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=volume")).getItemId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getSize().equals(disk.getSize()))
                .filter(e -> e.getParent().equals(volumeId))
                .filter(e -> e.getType().equals("snapshot"))
                .count(), "Item snapshot не соответствует условиям или не найден");

        new IndexPage().goToDisks().selectDisk(disk.getName());
        diskPage.attachComputeVolume(vm.getName(), false);

        String instanceId = StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("instance"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=instance")).getItemId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("snapshot"))
                .filter(e -> e.getParent().equals(volumeId))
                .count(), "Item snapshot не соответствует условиям или не найден");
        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
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

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item volume не соответствует условиям или не найден");

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> Objects.equals(e.getParent(), volumeId))
                .count(), "Item snapshot не соответствует условиям или не найден");

        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.LESS, diskPage::delete);
        Assertions.assertEquals(0, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .count());
    }

    @Test
    @TmsLink("1398386")
    @DisplayName("Cloud Compute. Создание снимка системного диска и удаление вместе с вм")
    void createSystemSnapshot() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setName(getRandomName())
                .setBootSize(7)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderIdVm = vmPage.getOrderId();
        Disk disk = vmPage.selectDisk(vmPage.getSystemDiskName());
        String orderIdDisk = disk.getOrderId();
        disk.createSnapshot(vm.getName());
        new IndexPage().goToSnapshots().selectSnapshot(vm.getName()).markForDeletion(new SnapshotEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);

        String volumeId = StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getType().equals("volume"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=volume")).getItemId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("snapshot"))
                .filter(e -> e.getParent().equals(volumeId))
                .count(), "Item snapshot не соответствует условиям или не найден");
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
        Waiting.sleep(10000);
        Assertions.assertEquals(0, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .count());
        Assertions.assertEquals(0, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> Objects.equals(e.getSize(), vm.getBootSize()))
                .count());
    }

    @Test
    @TmsLinks({@TmsLink("1248945"), @TmsLink("1398401")})
    @DisplayName("Cloud Compute. Подключение диска из снимка на базе подключенного диска")
    void createSnapshotFromAttachDisk() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .setName(getRandomName())
                .setBootSize(8)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        Disk diskPage = vmPage.selectDisk(vmPage.getSystemDiskName());
        diskPage.createSnapshot(vm.getName());
        Snapshot snapshot = new IndexPage().goToSnapshots().selectSnapshot(vm.getName()).markForDeletion(new SnapshotEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        snapshot.createDisk(vm.getName());
        Disk createdDisk = new IndexPage().goToDisks().selectDisk(vm.getName()).markForDeletion(new VolumeEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderIdDisk = createdDisk.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getSrcOrderId().equals(orderIdDisk))
                .filter(e -> e.getParent().isEmpty())
                .count(), "Item volume не соответствует условиям или не найден");

        createdDisk.runActionWithCheckCost(CompareType.EQUALS, () -> createdDisk.attachComputeVolume(vm.getName(), true));
    }

    @Test
    @TmsLink("1398404")
    @DisplayName("Cloud Compute. Создание диска из образа MarketPlace")
    void createDiskFromImage() {
        DiskCreate disk = new IndexPage().goToDisks().addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .setMarketPlaceImage(image)
                .setSize(5L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).markForDeletion(new VolumeEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderId = diskPage.getOrderId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> Objects.equals(i.getSize(), disk.getSize()))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & size == " + disk.getSize());

        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.ZERO, diskPage::delete);
        Assertions.assertEquals(0, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .count(), "Item disk не соответствует условиям или не найден");
    }

    @Test
    @TmsLink("1398406")
    @DisplayName("Cloud Compute. Создание диска из пользовательского образа")
    void createDiskFromUserImage() {
        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .setSize(2L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).markForDeletion(new VolumeEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createImage(disk.getName()));
        new IndexPage().goToImages().selectImage(disk.getName()).markForDeletion(new ImageEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);

        new IndexPage().goToDisks().selectDisk(disk.getName()).runActionWithCheckCost(CompareType.ZERO, diskPage::delete);
        DiskCreate newDisk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .setUserImage(disk.getName())
                .setSize(5L)
                .clickOrder();

        Disk newDiskPage = new DiskList().selectDisk(newDisk.getName()).markForDeletion(new VolumeEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        String orderId = newDiskPage.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> Objects.equals(i.getSize(), newDisk.getSize()))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & size == " + newDisk.getSize());

        new IndexPage()
                .goToDisks()
                .selectDisk(newDisk.getName())
                .runActionWithCheckCost(CompareType.ZERO, newDiskPage::delete);

        Assertions.assertEquals(0, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .count(), "Item disk не соответствует условиям или не найден");
    }

    @Test
    @TmsLink("1507767")
    @DisplayName("Cloud Compute. Создать вм. Создать диск. Создать снимок. Подключить диск. Удалить снимок. Отключить диск. Изменить подсеть")
    void scenario1() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setName(getRandomName())
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);

        DiskCreate disk = new IndexPage().goToDisks().addDisk().setAvailabilityZone(availabilityZone).setName(vm.getName()).setSize(4L).clickOrder();
        Disk diskPage = new DiskList().selectDisk(disk.getName()).markForDeletion(new VolumeEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);

        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(vm.getName()));
        new IndexPage().goToSnapshots().selectSnapshot(vm.getName()).markForDeletion(new SnapshotEntity(), AbstractEntity.Mode.AFTER_TEST);
        new IndexPage().goToDisks().selectDisk(disk.getName());
        diskPage.attachComputeVolume(vm.getName(), false);
        Snapshot snapshot = new IndexPage().goToSnapshots().selectSnapshot(vm.getName());
        snapshot.runActionWithCheckCost(CompareType.LESS, snapshot::delete);

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .selectDisk(disk.getName())
                .runActionWithCheckCost(CompareType.LESS, diskPage::detachComputeVolume);

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .getNetworkMenu()
                .updateSubnet(defaultNetwork, defaultSubNetwork);
    }
}
