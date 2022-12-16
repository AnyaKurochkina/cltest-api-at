package ui.t1.tests.cloudCompute;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.NotFoundException;
import ru.testit.annotations.BeforeAll;
import ru.testit.annotations.Title;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.LoginPage;
import ui.t1.pages.cloudCompute.SelectBox;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudCompute.*;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static ui.t1.pages.cloudCompute.Vm.DiskInfo.COLUMN_NAME;
import static ui.t1.pages.cloudCompute.Vm.DiskInfo.COLUMN_SYSTEM;

@ExtendWith(ConfigExtension.class)
@ExtendWith(BeforeAllExtension.class)
@Epic("Cloud Compute")
@Tags({@Tag("ui_cloud_compute")})
@Log4j2
public class UiCloudComputeTest extends Tests {
    Project project;
    String availabilityZone = "ru-central1-a";
    SelectBox.Image image = new SelectBox.Image("Ubuntu", "20.04");
    String hddTypeOne = "HDD";
    String hddTypeSecond = "HDD";
    String securityGroup = "default";
    String sshKey = "default1";

    public UiCloudComputeTest() {
//        Project project = Project.builder().isForOrders(true).build().createObject();
//        String parentFolder = AuthorizerSteps.getParentProject(project.getId());
//        this.project = Project.builder().projectName("Проект для тестов Cloud Compute").folderName(parentFolder).build().createObjectPrivateAccess();
//        this.project = Project.builder().id("proj-6opt7sq1fg").build();
        this.project = Project.builder().id("proj-2cdvptgjx7").build();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @BeforeAll
    public void beforeAll() {
        new IndexPage().goToSshKeys().addKey(sshKey, "root");
        IndexPage.go();
    }

    @Test
    @Owner("Checked")
    @DisplayName("Создание/Удаление публичного IP")
    void createPublicIp() {
        String ip = new IndexPage()
                .goToPublicIps()
                .addIp(availabilityZone);
        PublicIp ipPage = new PublicIpList().selectIp(ip).checkCreate();

        String orderId = ipPage.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> i.getFloatingIpAddress().equals(ip))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & floatingIpAddress == " + ip);

        ipPage.runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(project.getId()).stream()
                .noneMatch(e -> Objects.equals(e.getFloatingIpAddress(), ip)));
    }


    @Owner("Checked")
    @Test
    @DisplayName("Создание/Удаление диска")
    void createDisk() {
        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName("AT-UI-" + Math.abs(new Random().nextInt()))
                .setSize(2L)
                .clickOrder();

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

    @Owner("Checked")
    @Test
    @DisplayName("Создание/Удаление ВМ c одним доп диском (auto_delete = on) boot_disk_auto_delete = off")
    void createVm() {
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(false)
                .setBootSize(5)
                .addDisk(name, 2, hddTypeOne, true)
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderId = vmPage.getOrderId();

        final List<StateServiceSteps.ShortItem> items = StateServiceSteps.getItems(project.getId());
        Assertions.assertEquals(3, items.stream().filter(e -> e.getOrderId().equals(orderId))
                .filter(e -> e.getSrcOrderId().equals(""))
                .filter(e -> e.getParent().equals(items.stream().filter(i -> i.getType().equals("instance")).findFirst().orElseThrow(
                        () -> new NotFoundException("Не найден item с type=compute")).getItemId()))
                .filter(i -> i.getType().equals("nic") || i.getType().equals("volume"))
                .count(), "Должно быть 4 item's (nic & volume)");

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.LESS, vmPage::delete);

        final List<StateServiceSteps.ShortItem> items2 = StateServiceSteps.getItems(project.getId());
        Assertions.assertTrue(items2.stream().noneMatch(e -> e.getOrderId().equals(orderId)), "Существуют item's с orderId=" + orderId);
        Assertions.assertEquals(1, items2.stream().filter(i -> Objects.nonNull(i.getName()))
                .filter(i -> i.getName().startsWith(vm.getName()))
                .filter(e -> {
                    if (!e.getOrderId().equals(e.getSrcOrderId()))
                        return false;
                    if (!Objects.equals(e.getSize(), vm.getBootSize()))
                        return false;
                    return !Objects.nonNull(e.getParent());
                }).count(), "Должен быть один item с новим orderId, size и parent=null");

        new IndexPage().goToDisks().selectDisk(name).runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    @Test
    @Owner("Checked")
    @DisplayName("Создание ВМ c двумя доп дисками (auto_delete = on и off) boot_disk_auto_delete = on")
    void createVm2() {
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setBootSize(5)
                .addDisk(name, 2, hddTypeOne, true)
                .addDisk(name, 3, hddTypeSecond, false)
                .setAvailabilityZone(availabilityZone)
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

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.LESS, vmPage::delete);

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
    @Owner("Checked")
    @DisplayName("Создание/Удаление ВМ c публичным IP")
    void createVmWidthPublicIp() {
        String ip = new IndexPage()
                .goToPublicIps()
                .addIp(availabilityZone);
        PublicIp ipPage = new PublicIpList().selectIp(ip).checkCreate();

        String orderIdIp = ipPage.getOrderId();

        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setBootSize(2)
                .setAvailabilityZone(availabilityZone)
                .setName(name)
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

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.LESS, vmPage::delete);

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdIp))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item publicIp должен вернуть свой OrderId + Parent=null");

        new IndexPage().goToPublicIps().selectIp(ip).runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(project.getId()).stream()
                .noneMatch(e -> Objects.equals(e.getFloatingIpAddress(), ip)));
    }

    @Test
    @Owner("Checked")
    @DisplayName("Подключить/Отключить диск")
    void attachDisk() {
        String vmName = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(vmName)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderIdVm = vmPage.getOrderId();

        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName("AT-UI-" + Math.abs(new Random().nextInt()))
                .setSize(6L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        String orderIdDisk = diskPage.getOrderId();

        diskPage.runActionWithCheckCost(CompareType.EQUALS, () -> diskPage.attachComputeVolume(vm.getName(), true));

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getSrcOrderId().equals(orderIdDisk))
                .filter(e -> e.getSize().equals(6L))
                .count(), "Item volume не соответствует условиям или не найден");

        Disk updatedDiskPage = new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .selectDisk(disk.getName());
        updatedDiskPage.runActionWithCheckCost(CompareType.EQUALS, updatedDiskPage::detachComputeVolume);

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getSize().equals(6L))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item volume не соответствует условиям или не найден");

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.LESS, vmPage::delete);
        new IndexPage()
                .goToDisks()
                .selectDisk(disk.getName())
                .runActionWithCheckCost(CompareType.LESS, updatedDiskPage::delete);
    }

    @Test
    @Owner("Checked")
    @DisplayName("Подключить/Отключить IP")
    void attachIp() {
        String vmName = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(vmName)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderIdVm = vmPage.getOrderId();

        String ip = new IndexPage()
                .goToPublicIps()
                .addIp(availabilityZone);
        PublicIp ipPage = new PublicIpList().selectIp(ip).checkCreate();
        String orderIdIp = ipPage.getOrderId();
        ipPage.runActionWithCheckCost(CompareType.EQUALS, () -> ipPage.attachComputeIp(vm.getName()));

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getSrcOrderId().equals(orderIdIp))
                .filter(e -> e.getFloatingIpAddress().equals(ip))
                .count(), "Item ip не соответствует условиям или не найден");

        PublicIp newIpPage = new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .selectNetworkInterface()
                .selectIp(ip);
        newIpPage.runActionWithCheckCost(CompareType.EQUALS, newIpPage::detachComputeIp);

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdIp))
                .filter(e -> e.getFloatingIpAddress().equals(ip))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item ip не соответствует условиям или не найден");

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.LESS, vmPage::delete);
        new IndexPage()
                .goToPublicIps()
                .selectIp(ip)
                .runActionWithCheckCost(CompareType.LESS, newIpPage::delete);
    }

    @Test
    @Owner("Checked")
    @DisplayName("Подключить/Отключить диск со снимком к вм")
    void createSnapshotFromDetachDisk() {
        String vmName = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(vmName)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderIdVm = vmPage.getOrderId();


        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName("DISK-" + Math.abs(new Random().nextInt()))
                .setSize(4L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        String orderIdDisk = diskPage.getOrderId();

        String snapshotName = "SNAP-" + disk.getName();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(snapshotName));

        new IndexPage().goToSnapshots().selectSnapshot(snapshotName)/*.checkCreate()*/;

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

        Disk updateDisk = new IndexPage().goToDisks().selectDisk(disk.getName());
        updateDisk.runActionWithCheckCost(CompareType.EQUALS, () -> updateDisk.attachComputeVolume(vm.getName(), false));

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

        new IndexPage()
                .goToDisks()
                .selectDisk(disk.getName())
                .runActionWithCheckCost(CompareType.LESS, diskPage::delete);
        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .count());
        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    @Test
    @Owner("Checked")
    @DisplayName("Создание снимка системного диска и удаление вместе с вм")
    void createSnapshotFromAttachDisk() {
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .setBootSize(7)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String orderIdVm = vmPage.getOrderId();
        Disk disk = vmPage.selectDisk(new Vm.DiskInfo().getRowByColumnValue(COLUMN_SYSTEM, "Да").getValueByColumn(COLUMN_NAME));
        String orderIdDisk = disk.getOrderId();
        disk.runActionWithCheckCost(CompareType.MORE, () -> disk.createSnapshot(name));
        new IndexPage().goToSnapshots().selectSnapshot(name).checkCreate();

        String volumeId = StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getType().equals("volume"))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с type=volume")).getItemId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .filter(e -> e.getType().equals("snapshot"))
                .filter(e -> e.getParent().equals(volumeId))
                .count(), "Item snapshot не соответствует условиям или не найден");

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.ZERO, vmPage::delete);

        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdVm))
                .count());
        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> !Objects.equals(e.getSize(), vm.getBootSize()))
                .count());
    }

    @Test
    void name() {
        new IndexPage().goToVirtualMachine().getVmList().forEach(e -> new IndexPage().goToVirtualMachine().selectCompute(e).delete());
        new IndexPage().goToDisks().getDiskList().forEach(e -> new IndexPage().goToDisks().selectDisk(e).delete());
        new IndexPage().goToPublicIps().getIpList().forEach(e -> new IndexPage().goToPublicIps().selectIp(e).delete());

//        DiskCreatePage disk = new IndexPage()
//                .goDisks()
//                .addVm()
//                .setMarketPlaceImage(new SelectBox.Image("Ubuntu", "20.04"))
//                .setSize("5")
//                .setType("SSD")
//                .setName("Test-AT")
//                .clickOrder();
//
//        DiskPage diskPage = new DisksPage().selectDisk(disk.getName());


        System.out.println(1);
    }
}
