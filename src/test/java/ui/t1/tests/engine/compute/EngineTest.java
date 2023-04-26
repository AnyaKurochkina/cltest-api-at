package ui.t1.tests.engine.compute;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.authorizer.AuthorizerSteps;
import ui.elements.TypifiedElement;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.tests.engine.AbstractComputeTest;

@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Cloud Compute")
@Feature("Cloud Engine")
public class EngineTest extends AbstractComputeTest {

    public EngineTest() {
        Project projectOrders = Project.builder().isForOrders(true).build().createObject();
        String parentFolder = AuthorizerSteps.getParentProject(projectOrders.getId());
        project = Project.builder().projectName("Проект для EngineTest").folderName(parentFolder).build().createObjectPrivateAccess();
    }

    private void checkElementsEngine(Condition condition){
        IndexPage indexPage = new IndexPage();
        indexPage.getLinkDisks().getButton().shouldBe(condition);
        indexPage.getLinkVirtualMachines().getButton().shouldBe(condition);
        indexPage.getLinkSnapshots().getButton().shouldBe(condition);
        indexPage.getLinkNetworkInterfaces().getButton().shouldBe(condition);
        indexPage.getLinkNetworks().getButton().shouldBe(condition);
        indexPage.getLinkImages().getButton().shouldBe(condition);
        indexPage.getLinkSshKeys().getButton().shouldBe(condition);
        indexPage.getLinkSecurityGroups().getButton().shouldBe(condition);
        indexPage.getLinkPublicIps().getButton().shouldBe(condition);
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
        TypifiedElement.refresh();
        new IndexPage().goToCloudEngine().getBtnConnect().should(Condition.visible);
    }

    @AfterAll
    @Override
    public void afterAll() {
    }
}
