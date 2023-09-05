package ui.t1.tests.engine.compute;

import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.NetworkInterfaceList;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.vpc.Rule;
import ui.t1.pages.cloudEngine.vpc.SecurityGroup;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Cloud Compute")
@Feature("Сетевые интерфейсы")
public class NetworkInterfacesMultiSelectTest extends AbstractComputeTest {
    private final EntitySupplier<String> securityGroupSecond = lazy(() -> {
        String group = getRandomName();
        new IndexPage().goToSecurityGroups().addGroup(group, "desc").markForDeletion(AbstractEntity.Mode.AFTER_CLASS);
        new SecurityGroup().addRule().setDestination(new Rule.CidrDestination("10.2.0.0/24"))
                .setSubnetType("IPv4")
                .setProtocol("UDP")
                .setOpenPorts(new Rule.Port(80))
                .setDescription(getRandomName())
                .setRoute("Входящее")
                .clickAdd();
        return group;
    });

    @Test
    @TmsLink("")
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Групповые действия. Добавить группы безопасности")
    void multiAddSecurityGroup() {
        VmCreate vm = randomVm.get();
        VmCreate vmSecond = randomVm.copy().get();
        String groupSecond = securityGroupSecond.get();

        NetworkInterfaceList networkInterfaceListPage = new IndexPage().goToNetworkInterfaces();
        synchronized (historyMutex.get(getProjectId())) {
            networkInterfaceListPage.setInterfaceCheckbox(vm.getName()).setInterfaceCheckbox(vmSecond.getName()).addInSecurityGroup(groupSecond);
            Waiting.find(() -> NetworkInterfaceList.NetworkInterfaceTable.getSecurityGroups(vm.getName()).contains(groupSecond)
                    && NetworkInterfaceList.NetworkInterfaceTable.getSecurityGroups(vmSecond.getName()).contains(groupSecond), Duration.ofMinutes(1));
            networkInterfaceListPage.waitChangeStatus();
            final String action = "Изменить группы безопасности";
            networkInterfaceListPage.checkActionByIndex(1, action);
            networkInterfaceListPage.checkLastAction(action);
        }
    }

    @AfterAll
    void afterClass() {
        AbstractEntity.deleteCurrentClassEntities();
    }
}
