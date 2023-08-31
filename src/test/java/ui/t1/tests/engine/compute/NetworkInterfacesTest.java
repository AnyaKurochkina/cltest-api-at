package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.NetworkInterfaceList;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.tests.engine.AbstractComputeTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Cloud Compute")
@Feature("Сетевые интерфейсы")
public class NetworkInterfacesTest extends AbstractComputeTest {

    @Test
    @Order(1)
    @TmsLinks({@TmsLink("1280488"), @TmsLink("1249430")})
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Подключить/Отключить публичный IP")
    void attachIp() {
        VmCreate vm = randomVm.get();
        String ip = new IndexPage().goToPublicIps().addIp(region);
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.getMenuNetworkInterface(vm.getName()).attachIp(ip);
        networkInterfaceList.selectNetworkInterfaceByVm(vm.getName()).detachComputeIp(ip);
    }

    @Test
    @Order(2)
    @TmsLink("1280489")
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Изменить группы безопасности")
    void changeSecurityGroup() {
        VmCreate vm = randomVm.get();
        new IndexPage().goToSecurityGroups().addGroup(vm.getName(), "desc").markForDeletion();
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.getMenuNetworkInterface(vm.getName()).updateSecurityGroups(vm.getName());
    }

    @Test
    @Order(3)
    @TmsLink("1508998")
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Изменить подсеть")
    void changeSubnet() {
        VmCreate vm = randomVm.get();
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.getMenuNetworkInterface(vm.getName()).updateSubnet("default");
    }
}
