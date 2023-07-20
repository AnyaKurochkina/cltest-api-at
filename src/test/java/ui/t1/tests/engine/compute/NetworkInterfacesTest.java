package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Isolated;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.NetworkInterfaceList;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.PublicIpList;
import ui.t1.tests.engine.AbstractComputeTest;

@Isolated
@Epic("Cloud Compute")
@Feature("Сетевые интерфейсы")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NetworkInterfacesTest extends AbstractComputeTest {

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
        new VmList().selectCompute(vm.getName()).markForDeletion(new VmEntity()).checkCreate(true);
        String ip = new IndexPage().goToPublicIps().addIp(availabilityZone);
        new PublicIpList().selectIp(ip).markForDeletion(new PublicIpEntity()).checkCreate(true);
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.getMenuNetworkInterface(vm.getName()).attachIp(ip);
        networkInterfaceList.selectNetworkInterfaceByVm(vm.getName()).detachComputeIp(ip);
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
        new VmList().selectCompute(vm.getName()).markForDeletion(new VmEntity()).checkCreate(true);
        new IndexPage().goToSecurityGroups().addGroup(vm.getName(), "desc").markForDeletion();
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.getMenuNetworkInterface(vm.getName()).updateSecurityGroups(vm.getName());
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
        new VmList().selectCompute(vm.getName()).markForDeletion(new VmEntity()).checkCreate(true);
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.getMenuNetworkInterface(vm.getName()).updateSubnet("default");
    }
}
