package ui.t1.tests.engine.compute;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.authorizer.AuthorizerSteps;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.BeforeAllExtension;
import ui.t1.tests.engine.AbstractComputeTest;

@ExtendWith(BeforeAllExtension.class)
@Feature("Cloud Engine")
public class EngineTest extends AbstractComputeTest {

    public EngineTest() {
        Project projectOrders = Project.builder().isForOrders(true).build().createObject();
        String parentFolder = AuthorizerSteps.getParentProject(projectOrders.getId());
        project = Project.builder().projectName("Проект для EngineTest").folderName(parentFolder).build().createObjectPrivateAccess();
    }

    private void checkElementsEngine(Condition condition){
        IndexPage indexPage = new IndexPage();
        indexPage.getLinkDisks().shouldBe(condition);
        indexPage.getLinkVirtualMachines().shouldBe(condition);
        indexPage.getLinkSnapshots().shouldBe(condition);
        indexPage.getLinkNetworkInterfaces().shouldBe(condition);
        indexPage.getLinkNetworks().shouldBe(condition);
        indexPage.getLinkImages().shouldBe(condition);
        indexPage.getLinkSshKeys().shouldBe(condition);
        indexPage.getLinkSecurityGroups().shouldBe(condition);
        indexPage.getLinkPublicIps().shouldBe(condition);
    }

    @Test
    @Order(1)
    @TmsLink("982484")
    @DisplayName("T1 Cloud Engine. Создание")
    void connectEngine() {
        new IndexPage().goToCloudEngine().connectCloudCompute();
        checkElementsEngine(Condition.visible);
    }

    @Test
    @Order(2)
    @TmsLink("953813")
    @DisplayName("T1 Cloud Engine. Отключить")
    void disconnectEngine() {
        new IndexPage().disconnectCloudEngine();
        checkElementsEngine(Condition.not(Condition.visible));
    }
}
