package ui.t1.tests.engine.compute;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.compute.NetworkInterfaceList;
import ui.t1.pages.cloudEngine.compute.VmCreate;
import ui.t1.pages.cloudEngine.compute.VmList;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Cloud Compute")
@Feature("Сетевые интерфейсы")
public class NetworkInterfacesMultiSelectTest extends AbstractComputeTest {
    private final EntitySupplier<String> securityGroupSecond = lazy(() -> {
        String group = getRandomName();
        new IndexPage().goToSecurityGroups().addGroup(group, "desc").markForDeletion(AbstractEntity.Mode.AFTER_CLASS);
        return group;
    });

    @Test
    @TmsLink("")
    @DisplayName("Cloud Compute. Сетевые интерфейсы. Групповые действия. Добавить группы безопасности")
    void multiAddSecurityGroup() {
        VmCreate vm = randomVm.get();
        VmCreate vmSecond = randomVm.copy().get();
        String groupSecond = securityGroupSecond.get();

        NetworkInterfaceList networkInterfaceList = new IndexPage().goToNetworkInterfaces();
    }
}
