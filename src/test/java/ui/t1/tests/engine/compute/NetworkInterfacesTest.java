package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.api.parallel.ResourceLock;
import ui.cloud.pages.CompareType;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.pages.cloudEngine.compute.NetworkInterfaceList;
import ui.t1.pages.cloudEngine.compute.Vm;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.tests.engine.AbstractComputeTest;

import static core.utils.AssertUtils.assertHeaders;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ_WRITE;

@ExtendWith(BeforeAllExtension.class)
@Epic("Cloud Compute")
@Feature("Сетевые интерфейсы")
public class NetworkInterfacesTest extends AbstractComputeTest {

    @Test
    @TmsLink("1249429")
    @DisplayName("Cloud Compute. Сетевые интерфейсы")
    void networkInterfacesList() {
        new IndexPage().goToNetworkInterfaces();
        assertHeaders(new NetworkInterfaceList.NetworkInterfaceTable(), "", "IP адрес", "MAC адрес", "Сеть", "Подсеть", "Регион", "Группы безопасности", "Виртуальная машина", "");
    }

    @Test
    @TmsLinks({@TmsLink("1280488"), @TmsLink("1249430")})
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Подключить/Отключить публичный IP")
    void attachIp() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        new VmList().selectCompute(vm.getName()).checkCreate();
        String ip = new IndexPage().goToPublicIps().addIp(availabilityZone);
        createdIpList.add(ip);
        new PublicIpList().selectIp(ip).checkCreate();
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        executeWithHistoryLock(()-> networkInterfaceList.getMenuNetworkInterface(vm.getName()).attachIp(ip));
        networkInterfaceList.selectNetworkInterfaceByVm(vm.getName()).detachComputeIp(ip);
//        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
//        new IndexPage().goToPublicIps().selectIp(ip).runActionWithCheckCost(CompareType.LESS, ipPage::delete);
    }

    @Test
    @TmsLink("1280489")
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Изменить группы безопасности")
    void changeSecurityGroup() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        new IndexPage().goToSecurityGroups().addGroup(vm.getName(), "desc");
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        executeWithHistoryLock(() -> networkInterfaceList.getMenuNetworkInterface(vm.getName()).updateSecurityGroups(vm.getName()));
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
        new IndexPage().goToSecurityGroups().deleteGroup(vm.getName());
    }

    @Test
    @TmsLink("1508998")
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Изменить подсеть")
    void changeSubnet() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setAvailabilityZone(availabilityZone)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        executeWithHistoryLock(() -> networkInterfaceList.getMenuNetworkInterface(vm.getName()).updateSubnet("default"));
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
    }
}
