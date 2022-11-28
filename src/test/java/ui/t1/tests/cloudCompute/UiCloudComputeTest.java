package ui.t1.tests.cloudCompute;

import api.Tests;
import core.enums.Role;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.NotFoundException;
import ru.testit.annotations.Title;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.pages.LoginPage;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudCompute.*;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static ui.t1.pages.cloudCompute.PublicIpsPage.IpTable.COLUMN_IP;

@ExtendWith(ConfigExtension.class)
@Epic("Cloud Compute")
@Tags({@Tag("ui_cloud_compute")})
@Log4j2
public class UiCloudComputeTest extends Tests {
    Project project;

    public UiCloudComputeTest() {
//        Project project = Project.builder().isForOrders(true).build().createObject();
//        String parentFolder = AuthorizerSteps.getParentProject(project.getId());
//        this.project = Project.builder().projectName("Проект для тестов Cloud Compute").folderName(parentFolder).build().createObjectPrivateAccess();
        this.project = Project.builder().id("proj-6opt7sq1fg").build();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @Owner("Checked")
    @DisplayName("Создание/Удаление публичного IP")
    void createPublicIp() {
        new IndexPage()
                .goToPublicIps()
                .addIp("ru-central1-c");
        String ip = new PublicIpsPage.IpTable().getFirstValueByColumn(COLUMN_IP);
        PublicIpPage ipPage = new PublicIpsPage().selectIp(ip).checkCreate();

        String orderId = ipPage.getOrderId();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> i.getFloatingIpAddress().equals(ip))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & floatingIpAddress == " + ip);

        ipPage.runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(project.getId()).stream().filter(e -> Objects.nonNull(e.getFloatingIpAddress()))
                .noneMatch(e -> e.getFloatingIpAddress().equals(ip)));
    }


    @Owner("Checked")
    @Test
    @DisplayName("Создание/Удаление диска")
    void createDisk() {
        DiskCreatePage disk = new IndexPage()
                .goToDisks()
                .addDisk()
                .setAvailabilityZone("ru-central1-c")
                .setName("AT-UI-" + Math.abs(new Random().nextInt()))
                .setSize(2)
                .clickOrder();

        //Todo: Временный фикс
        Waiting.sleep(40000);
        DiskPage diskPage = new DisksPage().selectDisk(disk.getName()).checkCreate();

        String orderId = diskPage.getOrderId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> i.getSize().equals(disk.getSize()))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & size == " + disk.getSize());

        diskPage.runActionWithCheckCost(CompareType.ZERO, diskPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(project.getId()).stream().noneMatch(e -> e.getOrderId().equals(orderId)));
    }

    @Owner("Checked")
    @Test
    @DisplayName("Создание/Удаление ВМ c одним доп диском (auto_delete = on) boot_disk_auto_delete = off")
    void createVm() {
        new IndexPage().goToSshKeys().addKey("default", "root");
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreatePage vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(new SelectBox.Image("CirrOS", "1"))
                .setDeleteOnTermination(false)
                .setBootSize(5)
                .addDisk(name, 2, "SSD", true)
                .setAvailabilityZone("ru-central1-c")
                .setName(name)
                .addSecurityGroups("default")
                .setSshKey("default")
                .clickOrder();

        VmPage vmPage = new VmsPage().selectCompute(vm.getName()).checkCreate();
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
                    if (e.getSize() != 5)
                        return false;
                    return !Objects.nonNull(e.getParent());
                }).count(), "Должен быть один item с новим orderId, size=3 и parent=null");

        new IndexPage().goToDisks().selectDisk(name).delete();
    }

    @Test
    @Owner("Checked")
    @DisplayName("Создание ВМ c двумя доп дисками (auto_delete = on и off) boot_disk_auto_delete = on")
    void createVm2() {
        new IndexPage().goToSshKeys().addKey("default", "root");
        String name = "AT-UI-" + Math.abs(new Random().nextInt());
        VmCreatePage vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
                .setImage(new SelectBox.Image("CirrOS", "1"))
                .setDeleteOnTermination(true)
                .setBootSize(5)
                .addDisk(name, 2, "SSD", true)
                .addDisk(name, 3, "HDD", false)
                .setAvailabilityZone("ru-central1-c")
                .setName(name)
                .addSecurityGroups("default")
                .setSshKey("default")
                .clickOrder();

        VmPage vmPage = new VmsPage().selectCompute(vm.getName()).checkCreate();
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
        new IndexPage().goToDisks().selectDisk(name).delete();
    }


    @Test
    void name() {


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
