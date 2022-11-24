package ui.t1.tests.cloudCompute;

import api.Tests;
import core.enums.Role;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.NotFoundException;
import ru.testit.annotations.Title;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.LoginPage;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudCompute.*;

import java.util.List;
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
    void createPublicIp() {
        new IndexPage()
                .goToPublicIps()
                .addIp("ru-central1-c");
        String ip = new PublicIpsPage.IpTable().getFirstValueByColumn(COLUMN_IP);
        PublicIpPage ipPage = new PublicIpsPage().selectIp(ip);

        String orderId = ipPage.getOrderId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId())
                .filter(e -> e.getKey().equals(orderId))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с OrderId " + orderId)).getValue()
                        .stream()
                .filter(i -> i.getFloatingIpAddress().equals(ip))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                        .count(), "Поиск item, где orderId = srcOrderId & floatingIpAddress == " + ip);

//        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId())
//                        .filter(e -> e.getKey().equals(e.getValue().stream().filter(i -> i.getFloatingIpAddress().equals(ip))
//                                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с IP " + ip)).getSrcOrderId())).count(),
//                "Поиск item, где orderId = srcOrderId & floatingIpAddress == " + ip);


//        ipPage.runActionWithCheckCost(CompareType.ZERO, page::delete);
    }


    @Test
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

        DiskPage diskPage = new DisksPage().selectDisk(disk.getName());

        String orderId = diskPage.getOrderId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId())
                .filter(e -> e.getKey().equals(orderId))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с OrderId " + orderId)).getValue()
                .stream()
                .filter(i -> i.getSize().equals(disk.getSize()))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & size == " + disk.getSize());

//        diskPage = new IndexPage().goDisks().selectDisk(disk.getName());
//        diskPage.runActionWithCheckCost(CompareType.ZERO, diskPage::delete);

//        System.out.println(1);
    }

    @Test
    void createVm() {
        new IndexPage().goToSshKeys().addKey("default", "root");

        VmCreatePage vm = new IndexPage()
                .goToVirtualMachine()
                .addVm()
//                .set
                .setAvailabilityZone("ru-central1-c")
                .setName("AT-UI-" + Math.abs(new Random().nextInt()))
                .addSecurityGroups("default")
                .setImage(new SelectBox.Image("Ubuntu", "20.04"))
                .setSshKey("default")
                .clickOrder();

        VmPage vmPage = new VmsPage().selectCompute(vm.getName()).checkCreate();
        String orderId = vmPage.getOrderId();

        List<StateServiceSteps.ShortItem> items = StateServiceSteps.getItems(project.getId())
                .filter(e -> e.getKey().equals(orderId))
                .findFirst().orElseThrow(() -> new NotFoundException("Не найден item с OrderId " + orderId)).getValue();
        Assertions.assertEquals(2, items.stream()
                .filter(i -> i.getSrcOrderId().equals(""))
                .filter(i -> i.getParent().equals(items.stream().filter(e -> e.getType().equals("instance")).findFirst()
                        .orElseThrow(() -> new NotFoundException("Не найден item с type=compute")).getItemId()))
                .filter(i -> i.getType().equals("nic") || i.getType().equals("volume"))
                .count());
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
