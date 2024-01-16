package ui.t1.tests.engine.compute;

import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import steps.vpc.SecurityGroup;
import steps.vpc.SecurityGroupResponse;
import steps.vpc.VpcSteps;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.NetworkInterfaceList;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.tests.engine.AbstractComputeTest;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Cloud Compute")
@Feature("Сетевые интерфейсы")
public class InterfacesMultiSelectTest extends AbstractComputeTest {

    @Test
    @TmsLink("")
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Групповые действия. Добавить/Удалить группы безопасности")
    void multiAddSecurityGroup() {
        VmCreate vm = randomVm.get();
        VmCreate vmSecond = randomVm.copy().get();
        SecurityGroupResponse group = VpcSteps.createSecurityGroup(getProjectId(), SecurityGroup.builder().name(getRandomName()).description("multiAddSecurityGroup").build());
        group.setProjectId(getProjectId());
        group.deleteMode(AbstractEntity.Mode.AFTER_TEST);
        NetworkInterfaceList networkInterfaceListPage = new IndexPage().goToNetworkInterfaces();
        synchronized (historyMutex.get(getProjectId())) {
            networkInterfaceListPage.setInterfaceCheckbox(vm.getName()).setInterfaceCheckbox(vmSecond.getName()).addInSecurityGroup(group.getName());
            Waiting.find(() -> NetworkInterfaceList.NetworkInterfaceTable.getSecurityGroups(vm.getName()).contains(group.getName())
                    && NetworkInterfaceList.NetworkInterfaceTable.getSecurityGroups(vmSecond.getName()).contains(group.getName()), Duration.ofMinutes(1));
            networkInterfaceListPage.waitChangeStatus();
            final String action = "Изменить группы безопасности";
            networkInterfaceListPage.checkActionByIndex(1, action);
            networkInterfaceListPage.checkLastAction(action);
        }
        synchronized (historyMutex.get(getProjectId())) {
            networkInterfaceListPage.setInterfaceCheckbox(vm.getName()).setInterfaceCheckbox(vmSecond.getName()).deleteFromSecurityGroup(group.getName());
            Waiting.find(() -> NetworkInterfaceList.NetworkInterfaceTable.getSecurityGroups(vm.getName()).contains(group.getName())
                    && NetworkInterfaceList.NetworkInterfaceTable.getSecurityGroups(vmSecond.getName()).contains(group.getName()), Duration.ofMinutes(1));
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
