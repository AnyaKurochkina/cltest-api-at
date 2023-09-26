package ui.t1.tests.engine.vpc;

import core.utils.AssertUtils;
import core.utils.ssh.SshClient;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.EnabledIfEnv;
import org.junit.jupiter.api.*;
import ui.cloud.pages.CompareType;
import ui.elements.Table;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.SshKeyList;
import ui.t1.pages.cloudEngine.compute.Vm;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.vpc.VirtualIp;
import ui.t1.pages.cloudEngine.vpc.VirtualIpCreate;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Виртуальные IP")
@Epic("Cloud Compute")
public class VirtualIpVmTest extends AbstractComputeTest {
    EntitySupplier<VirtualIpCreate> randomVip = lazy(() ->{
        VirtualIpCreate v = new IndexPage().goToVirtualIps().addIp().setRegion(region).setNetwork(defaultNetwork).setL2(true).setName(getRandomName())
                .setInternet(true).setMode("active-active").clickOrder();
        new IndexPage().goToVirtualIps().selectIp(v.getIp())
                .markForDeletion(new VipEntity().deleteMode(AbstractEntity.Mode.AFTER_CLASS)).checkCreate(false);
        return v;
    });
    EntitySupplier<VmCreate> randomVmSecond = randomVm.copy();

    @Test
    @Order(1)
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Подключить/Отключить к нескольким сетевым интерфейсам")
    void connectVm() {
        VmCreate vm = randomVm.get();
        VmCreate vm2 = randomVmSecond.get();
        VirtualIpCreate vip = randomVip.get();

        String localIp = new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).getLocalIp();
        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).getMenu().attachComputeIp(localIp);
        new IndexPage().goToVirtualIps().selectIp(vip.getIp());
        Assertions.assertTrue(VirtualIp.InterfacesTable.isAttachIp(localIp), "В таблице 'Сетевые интерфейсы' не найден " + localIp);
        String localIp2 = new IndexPage().goToVirtualMachine().selectCompute(vm2.getName()).getLocalIp();
        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).getMenu().attachComputeIp(localIp2);

        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).getMenu().detachComputeIp(localIp2);
        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).getMenu().detachComputeIp(localIp);
        new IndexPage().goToVirtualIps().selectIp(vip.getIp());
        Assertions.assertFalse(Table.isExist(Column.DIRECTION), "Таблица 'Сетевые интерфейсы' должна отсутствовать");
    }

    @Test
    @Order(2)
    @EnabledIfEnv("t1prod")
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Доступ в интернет (при заказе)")
    void checkVirtualIp() {
        VmCreate vm2 = randomVmSecond.get();
        VirtualIpCreate vip = randomVip.get();
        String localIp2 = new IndexPage().goToVirtualMachine().selectCompute(vm2.getName()).getLocalIp();

        VirtualIp vipPage = new IndexPage().goToVirtualIps().selectIp(vip.getIp());
        vipPage.getMenu().attachComputeIp(localIp2);
        vipPage.getBtnGeneralInfo().click();
        String publicIp = vipPage.getPublicIpElement().nextItem().getText();
        SshClient.SshClientBuilder ssh = SshClient.builder().host(publicIp).user(SshKeyList.SSH_USER);
        AssertUtils.assertContains(ssh.privateKey(SshKeyList.PRIVATE_KEY).build().execute("curl -Is http://yandex.ru"), "302 Moved temporarily");
    }

    @Test
    @Order(90)
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Проверка подключения")
    void checkInternet() {
        VmCreate vm = publicIpVm.get();
        VmCreate vm2 = randomVmSecond.get();
        VirtualIpCreate vip = randomVip.get();

        String localIp = new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).getLocalIp();
        String localIp2 = new IndexPage().goToVirtualMachine().selectCompute(vm2.getName()).getLocalIp();

        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).getMenu().attachComputeIp(localIp2);

        SshClient.SshClientBuilder ssh = SshClient.builder().host(vm.getPublicIp()).user(SshKeyList.SSH_USER);
        String addIpCmd = ssh.privateKey(SshKeyList.PRIVATE_KEY).build()
                .execute("ip addr add {}/32 dev $(ip -o addr show | awk -v ip=\"{}\" '$0 ~ ip {print $2}')", vip.getIp(), localIp);
        Assertions.assertEquals(addIpCmd, "", "Ошибка при добавлении IP адреса на интерфейс");
        String checkConnectCmd = ssh.privateKey(SshKeyList.PRIVATE_KEY).build()
                .execute("timeout 2 telnet {} 22", vip.getIp());
        AssertUtils.assertContains(checkConnectCmd, "OpenSSH");
    }

    @Test
    @TmsLink("")
    @Order(100)
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Удалить подключенный VIP")
    void deleteIp() {
        VmCreate vm2 = randomVmSecond.get();
        VirtualIpCreate vip = randomVip.get();
        String localIp2 = new IndexPage().goToVirtualMachine().selectCompute(vm2.getName()).getLocalIp();

        VirtualIp ipPage = new IndexPage().goToVirtualIps().selectIp(vip.getIp());
        ipPage.getMenu().attachComputeIp(localIp2);
        ipPage.runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vm2.getName());
        vmPage.runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    @AfterAll
    void afterClass() {
        AbstractEntity.deleteCurrentClassEntities();
    }
}
