package ui.t1.tests.engine.vpc;

import io.qameta.allure.*;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.Vm;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.PublicIp;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

import java.util.Objects;

import static core.utils.AssertUtils.assertHeaders;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Публичные IP")
@Epic("Cloud Compute")
public class PublicIpTest extends AbstractComputeTest {

    EntitySupplier<String> ipSup = lazy(() -> {
        String ip = new IndexPage().goToPublicIps().addIp(region);
        new PublicIpList().selectIp(ip).markForDeletion(new PublicIpEntity(), AbstractEntity.Mode.AFTER_CLASS).checkCreate(true);
        return ip;
    });

    @Test
    @TmsLink("1249436")
    @DisplayName("Cloud VPC. Публичные IP-адреса")
    void publicIpList() {
        new IndexPage().goToPublicIps();
        assertHeaders(new PublicIpList.IpTable(), "", Column.IP_ADDRESS, "Регион", "Сетевой интерфейс", "Дата создания", "");
    }

    @Test
    @Order(1)
    @Tag("smoke")
    @Tag("health_check")
    @TmsLinks({@TmsLink("1249437"), @TmsLink("1249598")})
    @DisplayName("Cloud VPC. Публичные IP-адреса. Создать IP-адрес")
    void addIp() {
        String ip = openIp();
        PublicIp ipPage = new IndexPage().goToPublicIps().selectIp(ip)
                .markForDeletion(new PublicIpEntity(), AbstractEntity.Mode.AFTER_CLASS).checkCreate(true);
        String orderId = ipPage.getOrderId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> i.getFloatingIpAddress().equals(ip))
                .filter(i -> i.getSrcOrderId().equals(orderId))
                .count(), "Поиск item, где orderId = srcOrderId & floatingIpAddress == " + ip);
    }

    @Test
    @Order(2)
    @TmsLinks({@TmsLink("1249438"), @TmsLink("1249439"), @TmsLink("1248950")})
    @DisplayName("Cloud VPC. Публичные IP-адреса. Подключить к виртуальной машине/Отвязать от сетевого интерфейса")
    void attachIp() {
        String ip = openIp();
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_CLASS).checkCreate(true);
        String orderIdVm = vmPage.getOrderId();

        PublicIp ipPage =  new IndexPage().goToPublicIps().selectIp(ip);
        String orderIdIp = ipPage.getOrderId();
        ipPage.attachComputeIp(vm.getName());

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
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

        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderIdIp))
                .filter(e -> e.getFloatingIpAddress().equals(ip))
                .filter(e -> Objects.isNull(e.getParent()))
                .count(), "Item ip не соответствует условиям или не найден");
    }

    @Test
    @TmsLink("1249440")
    @Order(100)
    @DisplayName("Cloud VPC. Публичные IP-адреса. Освободить")
    void deleteIp() {
        String ip = openIp();
        PublicIp ipPage = new PublicIp();
        ipPage.runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(getProjectId()).stream().noneMatch(e -> Objects.equals(e.getFloatingIpAddress(), ip)));
    }

    @Step("Открыть страницу IP")
    private String openIp() {
        String ip = ipSup.get();
        new IndexPage().goToPublicIps().selectIp(ip);
        return ip;
    }
}
