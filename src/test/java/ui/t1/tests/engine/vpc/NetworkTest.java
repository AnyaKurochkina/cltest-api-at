package ui.t1.tests.engine.vpc;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.vpc.Network;
import ui.t1.pages.cloudEngine.vpc.NetworkList;
import ui.t1.tests.engine.AbstractComputeTest;

import static core.utils.AssertUtils.assertHeaders;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("Сети")
public class NetworkTest extends AbstractComputeTest {
    String name = getRandomName();

    @Test
    @TmsLink("1129127")
    @Order(1)
    @DisplayName("Cloud VPC. Сети. Добавление сети")
    void addNetwork() {
        new IndexPage().goToNetworks().addNetwork(name, "desc");
    }

    @Test
    @Order(2)
    @TmsLink("982506")
    @DisplayName("Cloud VPC. Сети")
    void networkList() {
        new IndexPage().goToNetworks();
        assertHeaders(new NetworkList.NetworksTable(), "", "Имя", "Описание", "Статус", "Дата создания", "");
        new NetworkList().selectNetwork(name);
        assertHeaders(new Network.SubnetListInfo(), "Наименование", "IPv4 CIDR", "Gateway", "Зона доступности", "Статус", "Описание", "");
    }

    @Test
    @Order(3)
    @TmsLink("1149757")
    @DisplayName("Cloud VPC. Сети. Добавление подсети")
    void addSubnet() {
        new IndexPage().goToNetworks().selectNetwork(name).addSubnet()
                .setAvailabilityZone(availabilityZone)
                .setCidr("10.0.0.0")
                .setName(getRandomName())
                .setDesc("addSubnet")
                .setDhcp(true)
                .setPrefix(16)
                .clickAdd();
    }

    @Test
    @Order(4)
    @TmsLink("1149810")
    @DisplayName("Cloud VPC. Сети. Удаление подсети")
    void editSubnet() {
        Network.CreateSubnet subnet = new IndexPage().goToNetworks().selectNetwork(name).addSubnet()
                .setAvailabilityZone(availabilityZone)
                .setCidr("10.1.0.0")
                .setName(getRandomName())
                .setDesc("deleteSubnet")
                .setDhcp(true)
                .setPrefix(28)
                .clickAdd();
        new IndexPage().goToNetworks().selectNetwork(name).deleteSubnet(subnet.getName());
    }

    @Test
    @TmsLink("1129129")
    @Order(100)
    @DisplayName("Cloud VPC. Сети. Удаление сети")
    void deleteNetwork() {
        new IndexPage().goToNetworks().deleteNetwork(name);
    }
}
