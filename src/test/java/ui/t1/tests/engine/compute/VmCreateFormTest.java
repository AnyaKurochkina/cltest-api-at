package ui.t1.tests.engine.compute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.Network;
import ui.t1.pages.cloudEngine.vpc.NetworkList;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

import static core.helper.StringUtils.$x;

@Feature("Виртуальные машины. Форма заказа")
@Epic("Cloud Compute")
public class VmCreateFormTest extends AbstractComputeTest {
    SelenideElement requestedIpHelperText = $x("//*[@id='root_network_configuration_requested_ip-helper-text']");

    EntitySupplier<Network.CreateSubnet> subnetRandom = lazy(() -> {
        String networkName = getRandomName();
        new IndexPage().goToNetworks().addNetwork(networkName, "desc");
        new NetworkList().selectNetwork(networkName).markForDeletion(new NetworkEntity(), AbstractEntity.Mode.AFTER_TEST);
        return new IndexPage().goToNetworks().selectNetwork(networkName).addSubnet()
                .setRegion(region)
                .setCidr("10.0.4.0")
                .setName(networkName)
                .setDesc(networkName)
                .setDhcp(true)
                .setPrefix(28)
                .clickAdd();
    });

    @Test
    @TmsLink("")
    @DisplayName("Cloud Compute. ВМ. Задать IP-адрес сетевого интерфейса. Не принадлежащий подсети")
    void createVmWidthIpNotValid() {
        Network.CreateSubnet subnet = subnetRandom.get();
        new IndexPage().goToVirtualMachine().addVm()
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(subnet.getName())
                .setSubnet(subnet.getName())
                .setNetworkInterface("10.0.4.17");
        requestedIpHelperText.shouldBe(Condition.text("IP адрес не принадлежит выбранной подсети"));
    }


    @Test
    @TmsLink("")
    @DisplayName("Cloud Compute. ВМ. Задать IP-адрес сетевого интерфейса. IP свободен/занят")
    void createVmWidthIp() {
        Network.CreateSubnet subnet = subnetRandom.get();
        final String ip = "10.0.4.4";
        final VmCreate vmCreate = new IndexPage().goToVirtualMachine().addVm()
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(subnet.getName())
                .setSubnet(subnet.getName())
                .setImage(image)
                .setNetworkInterface(ip)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .clickOrder();
        String localIp = new VmList().selectCompute(vmCreate.getName())
                .markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST).checkCreate(true).getLocalIp();
        Assertions.assertEquals(ip, localIp, "IP машины не соответствует ожидаемому");
        new IndexPage().goToVirtualMachine().addVm()
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(subnet.getName())
                .setSubnet(subnet.getName())
                .setNetworkInterface(ip);
        requestedIpHelperText.shouldBe(Condition.text("IP адрес занят"));
    }
}
