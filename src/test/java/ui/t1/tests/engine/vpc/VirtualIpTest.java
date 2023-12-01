package ui.t1.tests.engine.vpc;

import com.jcraft.jsch.JSchException;
import core.helper.DataFileHelper;
import core.helper.TableChecker;
import core.utils.ssh.SshClient;
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
import ui.t1.pages.cloudEngine.compute.SshKeyList;
import ui.t1.pages.cloudEngine.compute.VmCreate;
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
    private final EntitySupplier<VirtualIpCreate> vipSup = lazy(() -> virtualIpCreateWidthVMac("77:77:77:00:00:01"));
    private final EntitySupplier<VirtualIpCreate> vipSupSlave = lazy(() -> virtualIpCreateWidthVMac("77:77:77:00:00:02"));

    private final EntitySupplier<Void> prepareVmWidthVip = lazy(() -> {
        virtualMachineCreate(vipSup.get(), randomVm.get());
        return null;
    });
    private final EntitySupplier<Void> prepareVmWidthVipSlave = lazy(() -> {
        virtualMachineCreate(vipSupSlave.get(), randomVm.copy().get());
        return null;
    });

    private VirtualIpCreate virtualIpCreateWidthVMac(String vMac) {
        VirtualIpCreate v = new IndexPage().goToVirtualIps().addIp().setRegion(region).setNetwork(defaultNetwork).setL2(true)
                .setVMac(vMac).setName(getRandomName()).setInternet(true).setMode("active-active").clickOrder();
        new IndexPage().goToVirtualIps().selectIp(v.getIp())
                .markForDeletion(new VipEntity(), AbstractEntity.Mode.AFTER_CLASS).checkCreate(false);
        return v;
    }

    private void virtualMachineCreate(VirtualIpCreate vip, VmCreate vm){
        String publicIp = randomPublicIp.get();

        String localIp = new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).getLocalIp();
        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).getMenu().attachComputeIp(localIp);
        new IndexPage().goToVirtualIps().selectIp(vip.getIp());
        Assertions.assertTrue(VirtualIp.InterfacesTable.isAttachIp(localIp), "В таблице 'Сетевые интерфейсы' не найден " + localIp);

        new IndexPage().goToPublicIps().selectIp(publicIp).attachComputeIp(vm.getName());

        String localIpSlaveVm = new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).getLocalIp();
        addIpToInterface(publicIp, localIpSlaveVm, vip.getIp());
        new IndexPage().goToPublicIps().selectIp(publicIp).detachComputeIp();
    }

    private void addIpToInterface(String publicIp, String localIp, String vip) {
        SshClient ssh = SshClient.builder().host(publicIp).user(SshKeyList.SSH_USER).privateKey(SshKeyList.PRIVATE_KEY).build();
        String addIpCmd = ssh.execute("ip addr add {}/32 dev $(ip -o addr show | awk -v ip=\"{}\" '$0 ~ ip {print $2}')", vip, localIp);
        Assertions.assertTrue(addIpCmd.isEmpty(), "Ошибка при добавлении IP адреса на интерфейс: " + addIpCmd);
    }

    private void checkConnectBySsh(String publicIp) {
        SshClient ssh = SshClient.builder().host(publicIp).user(SshKeyList.SSH_USER).privateKey(SshKeyList.PRIVATE_KEY).build();
        Assertions.assertEquals("Linux", ssh.execute("uname"));
    }

    @Test
    @Order(1)
    @TmsLink("")
    @Tag("smoke")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Создать IP-адрес")
    void addIp() {
        VirtualIpCreate ip = vipSup.get();
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
        VirtualIpCreate ip = vipSup.get();
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
        VirtualIpCreate ip = vipSup.get();
        new IndexPage().goToVirtualIps().selectIp(ip.getIp());
        new TableChecker()
                .add(Column.IP, e -> e.equals(ip.getIp()))
                .add("Имя", e -> e.equals(ip.getName()))
                .add("Тип", e -> e.equals("Виртуальный IP адрес"))
                .add("Статус", String::isEmpty)
              //  .add(Column.MAC, e -> StringUtils.isMatch("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", e))
                .add("", String::isEmpty)
                .check(() -> new Table(Column.IP).getRowByColumnValue(Column.IP, ip.getIp()));
    }

    @Test
    @Order(4)
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Проверка подключения")
    void checkConnect() {
        prepareVmWidthVip.run();
        String publicIp = new IndexPage().goToVirtualIps().selectIp(vipSup.get().getIp()).getPublicIpElement().nextItem().getText();
        checkConnectBySsh(publicIp);
    }

    @Test
    @Order(5)
    @Disabled
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Проверка соединения между вм c L2")
    void checkConnectL2() {
        prepareVmWidthVip.run();
        prepareVmWidthVipSlave.run();
        String publicIp = new IndexPage().goToVirtualIps().selectIp(vipSup.get().getIp()).getPublicIpElement().nextItem().getText();
        SshClient ssh = SshClient.builder().host(publicIp).user(SshKeyList.SSH_USER).privateKey(SshKeyList.PRIVATE_KEY).build();
        ssh.writeTextFile("key", DataFileHelper.read(SshKeyList.PRIVATE_KEY));
        ssh.execute("ssh {}", vipSupSlave.get().getIp());
    }

    @Test
    @Order(3)
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Доступ в интернет. Подключить/отключить (Действие)")
    void checkInternetAction() {
        prepareVmWidthVip.run();
        String publicIp = new IndexPage().goToVirtualIps().selectIp(vipSup.get().getIp()).getPublicIpElement().nextItem().getText();

        new IndexPage().goToVirtualIps().selectIp(vipSup.get().getIp())
                .runActionWithCheckCost(CompareType.EQUALS, () -> new VirtualIp().getMenu().disableInternet());
        Assertions.assertThrows(JSchException.class, () -> checkConnectBySsh(publicIp));
        new IndexPage().goToVirtualIps().selectIp(vipSup.get().getIp())
                .runActionWithCheckCost(CompareType.EQUALS, () -> new VirtualIp().getMenu().enableInternet());
        checkConnectBySsh(publicIp);
    }

    @Test
    @TmsLink("")
    @Order(100)
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Удалить")
    void deleteIp() {
        VirtualIpCreate ip = vipSup.get();
        VirtualIp ipPage = new IndexPage().goToVirtualIps().selectIp(ip.getIp());
        ipPage.runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Assertions.assertTrue(StateServiceSteps.getItems(getProjectId()).stream().noneMatch(e -> Objects.equals(e.getFloatingIpAddress(), ip.getIp())));
    }

    @AfterAll
    void afterClass() {
        AbstractEntity.deleteCurrentClassEntities();
    }
}
