package ui.t1.tests.cloudCompute;

import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Menu;
import ui.elements.TypifiedElement;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudCompute.*;

import java.util.Arrays;
import java.util.Objects;

import static ui.t1.pages.cloudCompute.Disk.DiskInfo.COLUMN_NAME;
import static ui.t1.pages.cloudCompute.Disk.DiskInfo.COLUMN_SYSTEM;
import static ui.t1.pages.cloudCompute.IProductT1Page.BLOCK_PARAMETERS;

public class NetworkInterfacesTest extends AbstractComputeTest {

    @Test
    @TmsLink("1249429")
    @DisplayName("Cloud Compute. Сетевые интерфейсы")
    void networkInterfacesList() {
        new IndexPage().goToNetworkInterfaces();
        Assertions.assertEquals(Arrays.asList("", "IP адрес", "MAC адрес", "Сеть", "Подсеть", "Зона доступности", "Группы безопасности", "Виртуальная машина", ""),
                new NetworkInterfaceList.NetworkInterfaceTable().getHeaders(), "Названия столбцов в таблице не совпадают");
    }

    @Test
    @TmsLinks({@TmsLink("1280488"), @TmsLink("1249430")})
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Подключить/Отключить публичный IP")
    void attachIp() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        String ip = new IndexPage().goToPublicIps().addIp(availabilityZone);
        PublicIp ipPage = new PublicIpList().selectIp(ip).checkCreate();
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.runActionWithCheckCost(CompareType.MORE, () -> networkInterfaceList.getMenuNetworkInterface(vm.getName()).attachIp(ip));
        networkInterfaceList.runActionWithCheckCost(CompareType.LESS, () -> networkInterfaceList.getMenuNetworkInterface(vm.getName()).detachIp());
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
        new IndexPage().goToPublicIps().selectIp(ip).runActionWithCheckCost(CompareType.LESS, ipPage::delete);
    }

    @Test
    @TmsLink("1280489")
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Изменить группы безопасности")
    void changeSecurityGroup() {
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setImage(image)
                .setDeleteOnTermination(true)
                .setAvailabilityZone(availabilityZone)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vm.getName()).checkCreate();
        new IndexPage().goToSecurityGroups().addGroup(vm.getName(), "desc");
        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
        networkInterfaceList.runActionWithCheckCost(CompareType.MORE, () -> networkInterfaceList.getMenuNetworkInterface(vm.getName()).updateSecurityGroups(vm.getName()));
        new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).runActionWithCheckCost(CompareType.LESS, vmPage::delete);
        new IndexPage().goToSecurityGroups().deleteGroup(vm.getName());
    }
}
