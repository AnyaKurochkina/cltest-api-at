package ui.t1.tests.engine.vpc;

import core.helper.StringUtils;
import core.helper.TableChecker;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.CompareType;
import ui.elements.Table;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.vpc.VirtualIp;
import ui.t1.pages.cloudEngine.vpc.VirtualIpCreate;
import ui.t1.pages.cloudEngine.vpc.VirtualIpList;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

import java.util.Objects;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Виртуальные IP")
@Epic("Cloud Compute")
public class VirtualIpTest extends AbstractComputeTest {
    private final EntitySupplier<VirtualIpCreate> randomVip = lazy(() -> {
        VirtualIpCreate ip = new IndexPage().goToVirtualIps().addIp().setRegion(region).setNetwork(defaultNetwork).setL2(true).setName(getRandomName())
                /* .setNetworkInterface("10.0.3.2")*/.setMode("active-active").clickOrder();
        new VirtualIpList().selectIp(ip.getIp()).markForDeletion(new VipEntity().deleteMode(AbstractEntity.Mode.AFTER_CLASS)).checkCreate(true);
        return ip;
    });

    @Test
    @Order(1)
    @TmsLink("")
    @Tag("smoke")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Создать IP-адрес")
    void addIp() {
        VirtualIpCreate ip = randomVip.get();
        String orderId = new IndexPage().goToVirtualIps().selectIp(ip.getIp()).getOrderId();
        Assertions.assertEquals(1, StateServiceSteps.getItems(getProjectId()).stream()
                .filter(e -> e.getOrderId().equals(orderId))
                .filter(i -> i.getFloatingIpAddress().equals(ip.getIp()))
                .count(), "Поиск item, где orderId = srcOrderId & floatingIpAddress == " + ip.getIp());
    }

    @Test
    @Order(2)
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса")
    void checkIp() {
        VirtualIpCreate ip = randomVip.get();
        new IndexPage().goToVirtualIps();
        new TableChecker()
                .add("", String::isEmpty)
                .add(Column.IP_ADDRESS, e -> e.equals(ip.getIp()))
                .add("Регион", e -> e.equals(ip.getRegion()))
                .add("Сеть", e -> e.equals(ip.getNetwork()))
                .add("Подсеть", e -> e.length() > 5)
                .add("Поддержка L2", e -> e.equals("Да"))
                .add("Режим", e -> e.equals(ip.getMode()))
                .add("Дата создания", e -> e.length() > 4)
                .add("", String::isEmpty)
                .check(() -> new VirtualIpList.IpTable().getRowByColumnValue(Column.IP_ADDRESS, ip.getIp()));
    }

    @Test
    @Order(3)
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Действия")
    void checkIpActions() {
        VirtualIpCreate ip = randomVip.get();
        new IndexPage().goToVirtualIps().selectIp(ip.getIp());
        new TableChecker()
                .add(Column.IP, e -> e.equals(ip.getIp()))
                .add("Имя", e -> e.equals(ip.getName()))
                .add("Тип", e -> e.equals("Виртуальный IP адрес"))
                .add("Статус", String::isEmpty)
                .add(Column.MAC, e -> StringUtils.isMatch("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", e))
                .add("", String::isEmpty)
                .check(() -> new Table(Column.IP).getRowByColumnValue(Column.IP, ip.getIp()));
    }

//    @Test
//    @Order(3)
//    @TmsLink("")
//    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Доступ в интернет. Подключить/отключить")
//    void checkInternetAction() {
//        VmCreate vm = randomVm.get();
//        VirtualIpCreate vip = randomVip.get();
//        String localIp = new VmList().selectCompute(vm.getName()).getLocalIp();
//        VirtualIp virtualIp = new IndexPage().goToVirtualIps().selectIp(vip.getIp());
//        virtualIp.getMenu().attachComputeIp(localIp);
//        SshClient.SshClientBuilder ssh = SshClient.builder().host(ip).user(SshKeyList.SSH_USER);
//        String checkConnectCmd = ssh.privateKey(SshKeyList.PRIVATE_KEY).build()
//                .execute("timeout 2 telnet {} 22", localIpVmTest);
//        virtualIp.getMenu()
//    }

    @Test
    @TmsLink("")
    @Order(100)
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Удалить")
    void deleteIp() {
        VirtualIpCreate ip = randomVip.get();
        VirtualIp ipPage = new IndexPage().goToVirtualIps().selectIp(ip.getIp());
        ipPage.runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(getProjectId()).stream().noneMatch(e -> Objects.equals(e.getFloatingIpAddress(), ip.getIp())));
    }

    @AfterAll
    void afterClass() {
        AbstractEntity.deleteCurrentClassEntities();
    }
}
