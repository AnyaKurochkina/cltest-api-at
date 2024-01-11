package ui.t1.tests.engine.compute;

import com.codeborne.selenide.Condition;
import core.enums.Role;
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
import ui.t1.pages.T1LoginPage;
import ui.t1.tests.engine.AbstractComputeTest;

import java.time.Duration;

@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("smoke")
@Epic("Cloud Compute")
@Feature("Cloud Engine")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EngineTest extends AbstractComputeTest {
    Project project;

    public EngineTest() {
        Project projectOrders = Project.builder().isForOrders(true).build().createObject();
        String parentFolder = AuthorizerSteps.getParentProject(projectOrders.getId());
        project = Project.builder().projectName("Проект для EngineTest").folderName(parentFolder).build().createObjectPrivateAccess();
    }

    @Override
    @BeforeEach
    public void auth(TestInfo info) {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @BeforeAll
    public static void beforeAll() {}

    private void checkElementsEngine(Condition condition){
        TypifiedElement.refreshPage();
        IndexPage indexPage = new IndexPage();
        indexPage.goToCloudEngine();
        indexPage.getLinkDisks().getButton().shouldBe(condition);
        indexPage.getLinkVirtualMachines().getButton().shouldBe(condition);
        indexPage.getLinkSnapshots().getButton().shouldBe(condition);
        indexPage.getLinkNetworkInterfaces().getButton().shouldBe(condition);
        indexPage.getLinkImages().getButton().shouldBe(condition);
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
        TypifiedElement.refreshPage();
        new IndexPage().goToCloudEngine().getBtnConnect().should(Condition.visible, Duration.ofMinutes(2));
    }
}
