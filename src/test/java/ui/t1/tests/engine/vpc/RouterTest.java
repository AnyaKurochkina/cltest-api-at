package ui.t1.tests.engine.vpc;

import core.helper.Configure;
import core.helper.StringUtils;
import core.helper.TableChecker;
import core.utils.AssertUtils;
import core.utils.ssh.SshClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import ui.cloud.pages.CompareType;
import ui.elements.Breadcrumb;
import ui.elements.Menu;
import ui.elements.TypifiedElement;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.SshKeyList;
import ui.t1.pages.cloudEngine.compute.Vm;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.pages.cloudEngine.vpc.NetworkList;
import ui.t1.pages.cloudEngine.vpc.Router;
import ui.t1.pages.cloudEngine.vpc.RouterCreate;
import ui.t1.pages.cloudEngine.vpc.RouterList;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

import java.util.Arrays;

import static ui.cloud.pages.orders.IProductPage.getActionsMenuButton;
import static ui.t1.pages.IProductT1Page.BLOCK_PARAMETERS;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Маршрутизаторы")
@Epic("Cloud Compute")
public class RouterTest extends AbstractComputeTest {
    protected final EntitySupplier<RouterCreate> randomRouter = lazy(() -> {
        RouterCreate r = new IndexPage().goToRouters().addRouter().setName(getRandomName()).setRegion(region).setDesc("desc").addNetwork(defaultNetwork).clickOrder();
        new RouterList().selectRouter(r.getName()).markForDeletion(new ComputeEntity(), AbstractEntity.Mode.AFTER_CLASS).checkCreate(true);
        return r;
    });

    @Test
    @Order(1)
    @TmsLink("")
    @Tag("smoke")
    @DisplayName("Cloud VPC. Маршрутизаторы. Создать маршрутизатор")
    void addRouter() {
        RouterCreate router = randomRouter.get();
        new RouterList().selectRouter(router.getName()).markForDeletion(new ComputeEntity(), AbstractEntity.Mode.AFTER_CLASS).checkCreate(true);
    }

    @Test
    @Order(2)
    @TmsLink("")
    @DisplayName("Cloud VPC. Маршрутизаторы")
    void checkRouterList() {
        RouterCreate router = randomRouter.get();
        new IndexPage().goToRouters();
        new TableChecker()
                .add("", String::isEmpty)
                .add(Column.NAME, e -> e.equals(router.getName()))
                .add("Регион", e -> e.equals(router.getRegion()))
                .add("Публичный IP", e -> StringUtils.isMatch("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", e))
                .add("Подключенные сети", e -> e.equals(router.getNetworks().get(0)))
                .add("Дата создания", e -> e.length() > 4)
                .add("", String::isEmpty)
                .check(() -> new RouterList.RouterTable().getRowByColumnValue(Column.NAME, router.getName()));
    }

    @Test
    @Order(3)
    @TmsLink("")
    @DisplayName("Cloud VPC. Маршрутизатор. Действия")
    void checkRouter() {
        RouterCreate router = randomRouter.get();
        new IndexPage().goToRouters().selectRouter(router.getName());
        new TableChecker()
                .add(Column.NAME, e -> e.equals(router.getName()))
                .add("Тип", e -> e.equals("Маршрутизатор"))
                .add("Статус", String::isEmpty)
                .add("", String::isEmpty)
                .check(() -> new Router.RouterTable().getRow(0));
        new TableChecker()
                .add(Column.IP, e -> StringUtils.isMatch("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", e))
                .add(Column.NAME, e -> e.length() > 5)
                .add("Тип", e -> e.equals("Сетевой интерфейс"))
                .add("Статус", String::isEmpty)
                .add("Подсеть", e -> e.length() > 5)
                .add("Сеть", e -> e.equals(router.getNetworks().get(0)))
                .add(Column.MAC, e -> StringUtils.isMatch("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", e))
                .add("", String::isEmpty)
                .add("", String::isEmpty)
                .check(() -> new Router.NetworkInterfacesTable().getRow(0));
        AssertUtils.assertEqualsList(Arrays.asList("Подключить сеть", "Скопировать ID", "Удалить"), new Menu(getActionsMenuButton(BLOCK_PARAMETERS)).click().getOptions());
        TypifiedElement.refreshPage();
        AssertUtils.assertEqualsList(Arrays.asList("Удалить сетевой интерфейс", "Скопировать ID"), new Menu(new Router.NetworkInterfacesTable().getNetworkMenu(router.getNetworks().get(0))).click().getOptions());
    }

    @Test
    @Order(4)
    @TmsLink("SOUL-9277")
    @DisplayName("Cloud VPC. Маршрутизатор. Подключение к интернету")
    void connectNetwork() {
        randomRouter.get();
        VmCreate vmWidthIp = new IndexPage().goToVirtualMachine().addVm()
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setCreateIp()
                .setSshKey(sshKey)
                .clickOrder();
        Vm vmPage = new VmList().selectCompute(vmWidthIp.getName())
                .markForDeletion(new InstanceEntity(true), AbstractEntity.Mode.AFTER_TEST)
                .checkCreate(true);
        String localIpVmTest = vmPage.getLocalIp();

        String ip = vmPage.selectNetworkInterface().getPublicIp();
        new PublicIpEntity(getProjectId(), ip).deleteMode(AbstractEntity.Mode.AFTER_TEST);

        SshClient ssh = SshClient.builder().host(ip).user(SshKeyList.SSH_USER).privateKey(SshKeyList.PRIVATE_KEY).build();
        String checkConnectCmd = ssh.execute("timeout 2 telnet {} 22", localIpVmTest);
        AssertUtils.assertContains(checkConnectCmd, "OpenSSH");
        if (Configure.ENV.equalsIgnoreCase("t1prod"))
            AssertUtils.assertContains(ssh.execute(CONNECT_INTERNET_COMMAND), CONNECT_INTERNET_COMMAND_RESPONSE);
        Breadcrumb.click("Сервер");
        vmPage.delete(true, vmPage.getSystemDiskName());
    }

    @Test
    @Order(4)
    @TmsLink("")
    @DisplayName("Cloud VPC. Подключить/отключить сеть")
    void checkConnect() {
        RouterCreate router = randomRouter.get();
        final String networkName = getRandomName();
        new IndexPage().goToNetworks().addNetwork(networkName, "desc");
            new NetworkList().selectNetwork(networkName).markForDeletion(new NetworkEntity(), AbstractEntity.Mode.AFTER_TEST).addSubnet()
                    .setRegion(region)
                    .setCidr("10.1.1.0")
                    .setName(getRandomName())
                    .setDesc("forRouterTest")
                    .setDhcp(true)
                    .setPrefix(28)
                    .clickAdd();

            new IndexPage().goToRouters()
                    .getMenuRouter(router.getName())
                    .attachNetwork(networkName);
        Router routerPage = new IndexPage().goToRouters().selectRouter(router.getName());
        new Router.NetworkInterfacesTable().getRowByColumnValue(Column.NETWORK, networkName);

        new IndexPage().goToRouters().selectRouter(router.getName());
        routerPage.runActionWithCheckCost(CompareType.EQUALS, () -> routerPage.detachNetwork(networkName));
        new IndexPage().goToRouters().selectRouter(router.getName());
        Assertions.assertFalse(new Router.NetworkInterfacesTable().isColumnValueEquals(Column.NETWORK, networkName));
    }

    @Test
    @TmsLink("")
    @Order(100)
    @DisplayName("Cloud VPC. Маршрутизаторы. Удалить")
    void deleteRouter() {
        RouterCreate router = randomRouter.get();
        Router routerPage = new IndexPage().goToRouters().selectRouter(router.getName());
        routerPage.runActionWithCheckCost(CompareType.ZERO, routerPage::delete);
    }
}
