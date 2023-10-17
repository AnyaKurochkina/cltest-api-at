package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import ui.elements.Table;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.NetworkInterfaceList;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.vpc.NetworkList;
import ui.t1.tests.engine.AbstractComputeTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static ui.t1.pages.cloudEngine.compute.NetworkInterfaceList.NetworkInterfaceTable.COLUMN_VM;

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
        new IndexPage().goToSecurityGroups().addGroup(vm.getName(), "desc").markForDeletion(AbstractEntity.Mode.AFTER_TEST);
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.getMenuNetworkInterface(vm.getName()).updateSecurityGroups(vm.getName());
    }

    @Test
    @Order(3)
    @TmsLink("1508998")
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Изменить подсеть")
    void changeSubnet() {
        VmCreate vm = randomVm.get();
        String networkName = getRandomName();
        String ip = "10.0.3.6";
        new IndexPage().goToNetworks().addNetwork(networkName, "desc");
        new NetworkList().selectNetwork(networkName).markForDeletion(new NetworkEntity(), AbstractEntity.Mode.AFTER_TEST);
        new IndexPage().goToNetworks().selectNetwork(networkName).addSubnet()
                .setRegion(region)
                .setCidr("10.0.3.0")
                .setName(networkName)
                .setDesc(networkName)
                .setDhcp(true)
                .setPrefix(28)
                .clickAdd();
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.getMenuNetworkInterface(vm.getName()).updateSubnet(networkName, networkName, ip);
        new IndexPage().goToNetworkInterfaces();
        Table.Row row = new NetworkInterfaceList.NetworkInterfaceTable().getRowByColumnValue(COLUMN_VM, vm.getName());
        assertAll(() -> Assertions.assertEquals(ip, row.getValueByColumn("IP адрес"), "Значение в колонке IP адрес"),
                () -> Assertions.assertEquals(networkName, row.getValueByColumn(Column.NETWORK), "Значение в колонке " + Column.NETWORK),
                () -> Assertions.assertEquals(networkName, row.getValueByColumn("Подсеть"), "Значение в колонке Подсеть"));
    }
}
