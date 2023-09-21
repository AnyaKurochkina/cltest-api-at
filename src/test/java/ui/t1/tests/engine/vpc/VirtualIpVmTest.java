package ui.t1.tests.engine.vpc;

import core.helper.Configure;
import core.helper.DataFileHelper;
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
import ui.t1.pages.cloudEngine.vpc.PublicIp;
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
                .markForDeletion(new VipEntity(), AbstractEntity.Mode.AFTER_CLASS).checkCreate(false);
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
    @Order(89)
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Доступ в интернет. Подключить/отключить")
    void checkInternetAction() {
        VmCreate vmProxy = randomVm.get();
        VmCreate vm = randomVmSecond.get();
        String publicIp = randomPublicIp.get();
        VirtualIpCreate vip = new IndexPage().goToVirtualIps().addIp().setRegion(region).setNetwork(defaultNetwork).setL2(true).setName(getRandomName())
                .setInternet(false).setMode("active-active").clickOrder();
        new IndexPage().goToVirtualIps().selectIp(vip.getIp())
                .markForDeletion(new VipEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(false);

        String localIp = new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).getLocalIp();

        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).getMenu().attachComputeIp(localIp);
        new IndexPage().goToPublicIps().selectIp(publicIp).attachComputeIp(vm.getName());

        addIpToInterface(publicIp, localIp, vip);
        new IndexPage().goToPublicIps().selectIp(publicIp).runActionWithCheckCost(CompareType.LESS, () -> new PublicIp().detachComputeIp());
        new IndexPage().goToPublicIps().selectIp(publicIp).runActionWithCheckCost(CompareType.LESS, () -> new PublicIp().attachComputeIp(vmProxy.getName()));
//        checkConnectToVip(publicIp, vip);

        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).runActionWithCheckCost(CompareType.EQUALS, () -> new VirtualIp().getMenu().enableInternet());
        checkConnectToVip(publicIp, vip);

        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).runActionWithCheckCost(CompareType.EQUALS, () -> new VirtualIp().getMenu().disableInternet());
//        checkConnectToVip(publicIp, vip);
    }

    @Test
    @Order(90)
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Проверка подключения/Доступа к интернет")
    void checkConnect() {
        VmCreate vmProxy = randomVm.get();
        VmCreate vm = randomVmSecond.get();
        String publicIp = randomPublicIp.get();
        VirtualIpCreate vip = randomVip.get();

        String localIp = new IndexPage().goToVirtualMachine().selectCompute(vm.getName()).getLocalIp();
        new IndexPage().goToVirtualIps().selectIp(vip.getIp()).getMenu().attachComputeIp(localIp);
        new IndexPage().goToPublicIps().selectIp(publicIp).attachComputeIp(vm.getName());

        addIpToInterface(publicIp, localIp, vip);
        new IndexPage().goToPublicIps().selectIp(publicIp).detachComputeIp();
        new IndexPage().goToPublicIps().selectIp(publicIp).attachComputeIp(vmProxy.getName());
        checkConnectToVip(publicIp, vip);
    }

    @Test
    @TmsLink("")
    @Order(100)
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Удалить подключенный VIP")
    void deleteIp() {
        VmCreate vmProxy = randomVm.get();
        VirtualIpCreate vip = randomVip.get();
        String localIp2 = new IndexPage().goToVirtualMachine().selectCompute(vmProxy.getName()).getLocalIp();

        VirtualIp ipPage = new IndexPage().goToVirtualIps().selectIp(vip.getIp());
        ipPage.getMenu().attachComputeIp(localIp2);
        ipPage.runActionWithCheckCost(CompareType.ZERO, ipPage::delete);
        Vm vmPage = new IndexPage().goToVirtualMachine().selectCompute(vmProxy.getName());
        vmPage.runActionWithCheckCost(CompareType.ZERO, vmPage::delete);
    }

    private void addIpToInterface(String publicIp, String localIp, VirtualIpCreate vip){
        SshClient ssh = SshClient.builder().host(publicIp).user(SshKeyList.SSH_USER).privateKey(SshKeyList.PRIVATE_KEY).build();
        String addIpCmd = ssh.execute("ip addr add {}/32 dev $(ip -o addr show | awk -v ip=\"{}\" '$0 ~ ip {print $2}')",
                vip.getIp(), localIp);
        Assertions.assertTrue(addIpCmd.isEmpty() || addIpCmd.contains("File exists"), "Ошибка при добавлении IP адреса на интерфейс: " + addIpCmd);
    }

    private void checkConnectToVip(String publicIp, VirtualIpCreate vip){
        SshClient ssh = SshClient.builder().host(publicIp).user(SshKeyList.SSH_USER).privateKey(SshKeyList.PRIVATE_KEY).build();
        final String privateKeyFile = "private_key";
        ssh.writeTextFile(privateKeyFile, DataFileHelper.read(SshKeyList.PRIVATE_KEY));
        ssh.execute("chmod 400 {}", privateKeyFile);
        String res = ssh.execute("ssh -i {} {}@{} -o \"StrictHostKeyChecking no\" 'uname'", privateKeyFile, SshKeyList.SSH_USER, vip.getIp());
        AssertUtils.assertContains(res, "Linux");
        if(Configure.ENV.equalsIgnoreCase("t1prod")){
            res = ssh.execute("ssh -i {} {}@{} -o \"StrictHostKeyChecking no\" 'curl -Is http://yandex.ru'", privateKeyFile, SshKeyList.SSH_USER, vip.getIp());
            AssertUtils.assertContains(res, "302 Moved temporarily");
        }
    }

    @AfterAll
    void afterClass() {
        AbstractEntity.deleteCurrentClassEntities();
    }
}
