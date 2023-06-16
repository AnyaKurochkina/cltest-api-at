package ui.t1.tests.engine.vpc;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.BlockTests;
import org.junit.IgnoreInterceptTestExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.cloudEngine.compute.Vm;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.PublicIp;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.Objects;

import static core.utils.AssertUtils.assertHeaders;

@BlockTests
@ExtendWith(BeforeAllExtension.class)
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Публичные IP")
@Epic("Cloud Compute")
public class PublicIpTest extends AbstractComputeTest {
    String ip;

    @Test
    @IgnoreInterceptTestExtension
    @TmsLink("1249436")
    @DisplayName("Cloud VPC. Публичные IP-адреса")
    void publicIpList() {
        new IndexPage().goToPublicIps();
        assertHeaders(new PublicIpList.IpTable(), "", "IP-адрес", "Зона доступности", "Сетевой интерфейс", "");
    }

    @Test
    @Order(1)
    @Tag("health_check")
    @TmsLinks({@TmsLink("1249437"), @TmsLink("1249598")})
    @DisplayName("Cloud VPC. Публичные IP-адреса. Создать IP-адрес")
    void addIp() {
        ip = new IndexPage().goToPublicIps().addIp(availabilityZone);
        createdIpList.add(ip);
        PublicIp ipPage = new PublicIpList().selectIp(ip).checkCreate();
        String orderId = ipPage.getOrderId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> i.getFloatingIpAddress().equals(ip))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & floatingIpAddress == " + ip);
    }

    @Test
    @Order(2)
    @TmsLinks({@TmsLink("1249438"), @TmsLink("1249439"), @TmsLink("1248950")})
    @DisplayName("Cloud VPC. Публичные IP-адреса. Подключить к виртуальной машине/Отвязать от сетевого интерфейса ")
    void attachIp() {
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

        PublicIp ipPage =  new IndexPage().goToPublicIps().selectIp(ip);
        String orderIdIp = ipPage.getOrderId();
        ipPage.attachComputeIp(vm.getName());

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
        newIpPage.detachComputeIp();

        Assertions.assertEquals(1, StateServiceSteps.getItems(project.getId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdIp))
                .filter(e -> e.getFloatingIpAddress().equals(ip))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item ip не соответствует условиям или не найден");

        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
    }

    @Test
    @TmsLink("1249440")
    @Order(100)
    @DisplayName("Cloud VPC. Публичные IP-адреса. Освободить")
    void deleteIp() {
        PublicIp ipPage = new IndexPage().goToPublicIps().selectIp(ip);
        ipPage.runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(project.getId()).stream().noneMatch(e -> Objects.equals(e.getFloatingIpAddress(), ip)));
    }
}
