package ui.t1.tests.engine.vpc;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import ui.elements.Table;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.vpc.VirtualIp;
import ui.t1.pages.cloudEngine.vpc.VirtualIpCreate;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Виртуальные IP")
@Epic("Cloud Compute")
public class VirtualIpActionsTest extends AbstractComputeTest {
    EntitySupplier<VmCreate> randomVm2 = randomVm.copy();
    EntitySupplier<VirtualIpCreate> vipSup = lazy(() ->{
        VirtualIpCreate v = new IndexPage().goToVirtualIps().addIp().setRegion(region).setNetwork(defaultNetwork).setL2(false).setName(getRandomName())
                .setInternet(false).setMode("active-standby").clickOrder();
        new IndexPage().goToVirtualIps().selectIp(v.getIp())
                .markForDeletion(new VipEntity(), AbstractEntity.Mode.AFTER_CLASS).checkCreate(true);
        return v;
    });

    @Test
    @Order(1)
    @TmsLink("")
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Подключить/Отключить к нескольким сетевым интерфейсам")
    void connectVm() {
        VmCreate vm = randomVm.get();
        VmCreate vm2 = randomVm2.get();
        VirtualIpCreate vip = vipSup.get();

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
    @TmsLink("")
    @Order(100)
    @DisplayName("Cloud VPC. Виртуальные IP-адреса. Удалить подключенный VIP")
    void deleteIp() {
        VirtualIpCreate vip = vipSup.get();
        String localIpMaster = new IndexPage().goToVirtualMachine().selectCompute(randomVm.get().getName()).getLocalIp();

        VirtualIp ipPage = new IndexPage().goToVirtualIps().selectIp(vip.getIp());
        if (!VirtualIp.InterfacesTable.isAttachIp(localIpMaster))
            ipPage.getMenu().attachComputeIp(localIpMaster);
        ipPage.delete();
        new IndexPage().goToVirtualMachine().selectCompute(randomVm.get().getName()).delete();
    }
}
