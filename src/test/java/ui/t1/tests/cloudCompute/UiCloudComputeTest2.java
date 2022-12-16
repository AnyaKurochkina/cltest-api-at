package ui.t1.tests.cloudCompute;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.BeforeAll;
import ru.testit.annotations.Title;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.LoginPage;
import ui.t1.pages.cloudCompute.SelectBox;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudCompute.*;

import java.util.Objects;
import java.util.Random;

import static ui.t1.pages.cloudCompute.Vm.DiskInfo.COLUMN_NAME;
import static ui.t1.pages.cloudCompute.Vm.DiskInfo.COLUMN_SYSTEM;

@ExtendWith(ConfigExtension.class)
@ExtendWith(BeforeAllExtension.class)
@Epic("Cloud Compute")
@Tags({@Tag("ui_cloud_compute")})
@Log4j2
public class UiCloudComputeTest2 extends Tests {
    Project project;
    String availabilityZone = "ru-central1-a";
    SelectBox.Image image = new SelectBox.Image("Ubuntu", "20.04");
    String hddTypeOne = "HDD";
    String hddTypeSecond = "HDD";
    String securityGroup = "default";
    String sshKey = "default1";

    public UiCloudComputeTest2() {
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
    @Owner("checked")
    @DisplayName("Подключение диска из снимка на базе подключенного диска")
    void createSnapshotFromAttachDisk() {
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .setBootSize(8)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();

        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        Disk diskPage = vmPage.selectDisk(new Vm.DiskInfo().getRowByColumnValue(COLUMN_SYSTEM, "Да").getValueByColumn(COLUMN_NAME));
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createSnapshot(name));
        Snapshot snapshot = new IndexPage().goToSnapshots().selectSnapshot(name).checkCreate();
        snapshot.runActionWithCheckCost(CompareType.MORE, () -> snapshot.createDisk(name));
        Disk createdDisk = new IndexPage().goToDisks().selectDisk(name).checkCreate();
        String orderIdDisk = createdDisk.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdDisk))
                .filter(e -> e.getSrcOrderId().equals(orderIdDisk))
                .filter(e -> e.getParent().equals(""))
                .count(), "Item volume не соответствует условиям или не найден");

        createdDisk.runActionWithCheckCost(CompareType.EQUALS, () -> createdDisk.attachComputeVolume(vm.getName(), true));

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(vm.getName())
                .runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    @Test
    @Owner("checked")
    @DisplayName("Создание образа из диска")
    void createImageFromDisk() {
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .setSize(2L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createImage(name));
        Image imagePage = new IndexPage().goToImages().selectImage(name).checkCreate();
        String orderIdImage = imagePage.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdImage))
                .filter(e -> e.getSrcOrderId().equals(""))
                .filter(e -> e.getParent().equals(""))
                .count(), "Item image не соответствует условиям или не найден");

        new IndexPage()
                .goToDisks()
                .selectDisk(name)
                .runActionWithCheckCost(CompareType.ZERO, diskPage::delete);

        new IndexPage()
                .goToImages()
                .selectImage(name)
                .runActionWithCheckCost(CompareType.ZERO, imagePage::delete);

        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdImage))
                .count(), "Item image не соответствует условиям или не найден");
    }

    @Test
    @Owner("checked")
    @DisplayName("Создание диска из образа MarketPlace")
    void creatDiskFromImage() {
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(name)
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

        new IndexPage()
                .goToDisks()
                .selectDisk(name)
                .runActionWithCheckCost(CompareType.ZERO, diskPage::delete);

        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .count(), "Item disk не соответствует условиям или не найден");
    }

    @Test
    @Owner("checked")
    @DisplayName("Создание диска из пользовательского образа")
    void creatDiskFromUserImage() {
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .setSize(2L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createImage(name));
        Image imagePage = new IndexPage().goToImages().selectImage(name).checkCreate();

        new IndexPage()
                .goToDisks()
                .selectDisk(name)
                .runActionWithCheckCost(CompareType.ZERO, diskPage::delete);

        String newName = "AT-UI-" + Math.abs(new Random().nextInt());
        DiskCreate newDisk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(newName)
                .setUserImage(name)
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
                .selectDisk(newName)
                .runActionWithCheckCost(CompareType.ZERO, newDiskPage::delete);

        Assertions.assertEquals(0, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .count(), "Item disk не соответствует условиям или не найден");

        new IndexPage()
                .goToImages()
                .selectImage(name)
                .runActionWithCheckCost(CompareType.ZERO, imagePage::delete);
    }

    @Owner("Checked")
    @Test
    @DisplayName("Создание ВМ c пользовательским образом")
    void createVmWidthUserImage() {
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        DiskCreate disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .setSize(2L)
                .clickOrder();

        Disk diskPage = new DiskList().selectDisk(disk.getName()).checkCreate();
        diskPage.runActionWithCheckCost(CompareType.MORE, () -> diskPage.createImage(name));
        Image imagePage = new IndexPage().goToImages().selectImage(name).checkCreate();

        new IndexPage()
                .goToDisks()
                .selectDisk(name)
                .runActionWithCheckCost(CompareType.ZERO, diskPage::delete);

        VmCreate vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setUserImage(name)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(name)
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();

        new IndexPage()
                .goToVirtualMachine()
                .selectCompute(name)
                .runActionWithCheckCost(CompareType.ZERO, vmPage::delete);

        new IndexPage()
                .goToImages()
                .selectImage(name)
                .runActionWithCheckCost(CompareType.ZERO, imagePage::delete);
    }

    @Test
    void name() {
        new IndexPage().goToVirtualMachine().getVmList().forEach(e -> new IndexPage().goToVirtualMachine().selectCompute(e).delete());
        new IndexPage().goToDisks().getDiskList().forEach(e -> new IndexPage().goToDisks().selectDisk(e).delete());
        new IndexPage().goToImages().geImageList().forEach(e -> new IndexPage().goToImages().selectImage(e).delete());
        new IndexPage().goToSnapshots().geSnapshotList().forEach(e -> new IndexPage().goToSnapshots().selectSnapshot(e).delete());
        new IndexPage().goToPublicIps().getIpList().forEach(e -> new IndexPage().goToPublicIps().selectIp(e).delete());

//        new IndexPage().goToVirtualMachine().getVmList().parallelStream().forEach(e -> {
//            new LoginPage(project.getId()).signIn(Role.CLOUD_ADMIN);
//            new IndexPage().goToVirtualMachine().selectCompute(e).delete();
//        });
//        new IndexPage().goToDisks().getDiskList().parallelStream().forEach(e -> {
//            new LoginPage(project.getId()).signIn(Role.CLOUD_ADMIN);
//            new IndexPage().goToDisks().selectDisk(e).delete();
//        });
//        new IndexPage().goToImages().geImageList().parallelStream().forEach(e -> {
//            new LoginPage(project.getId()).signIn(Role.CLOUD_ADMIN);
//            new IndexPage().goToImages().selectImage(e).delete();
//        });
//        new IndexPage().goToSnapshots().geSnapshotList().parallelStream().forEach(e -> {
//            new LoginPage(project.getId()).signIn(Role.CLOUD_ADMIN);
//            new IndexPage().goToSnapshots().selectSnapshot(e).delete();
//        });
//        new IndexPage().goToPublicIps().getIpList().parallelStream().forEach(e -> {
//            new LoginPage(project.getId()).signIn(Role.CLOUD_ADMIN);
//            new IndexPage().goToPublicIps().selectIp(e).delete();
//        });


        System.out.println(1);
    }
}
