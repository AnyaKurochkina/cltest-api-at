package ui.t1.tests.cloudEngine.vpc;

import io.qameta.allure.TmsLink;
import org.junit.BlockTests;
import org.junit.IgnoreInterceptTestExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.NetworkInterfaceList;
import ui.t1.pages.cloudEngine.vpc.NetworkList;
import ui.t1.tests.cloudEngine.compute.AbstractComputeTest;

import static core.utils.AssertUtils.AssertHeaders;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NetworkTest extends AbstractComputeTest {
    String name = getRandomName();

    @Test
    @TmsLink("982506")
    @DisplayName("Cloud VPC. Сети")
    @IgnoreInterceptTestExtension
    void networkList() {
        new IndexPage().goToNetworks();
        AssertHeaders(new NetworkList.NetworksTable(), "", "Имя", "Описание", "Статус", "Дата создания", "");
    }

    @Test
    @TmsLink("1129127")
    @Order(1)
    @DisplayName("Cloud VPC. Сети. Добавление сети")
    void addNetwork() {
        new IndexPage().goToNetworks().addNetwork(name, "desc");
    }

    @Test
    @TmsLink("1129129")
    @Order(100)
    @DisplayName("Cloud VPC. Сети. Удаление сети")
    void deleteNetwork() {
        new IndexPage().goToNetworks().deleteNetwork(name);
    }
//
//    @Test
////    @TmsLink("")
//    @Order(2)
//    @DisplayName("Cloud Compute. Удаление SSH-ключа (личный)")
//    void deleteKey() {
//        new IndexPage().goToProfile().getSshKeys().deleteKey(name);
//    }
}
