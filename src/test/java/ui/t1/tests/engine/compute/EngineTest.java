package ui.t1.tests.engine.compute;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import steps.authorizer.AuthorizerSteps;
import ui.elements.TypifiedElement;
import ui.t1.pages.IndexPage;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

import java.time.Duration;

@Tag("smoke")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Cloud Compute")
@Feature("Cloud Engine")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EngineTest extends AbstractComputeTest {
    private final String projectId;

    private final EntitySupplier<Void> engineSup = lazy(() -> {
        new IndexPage().goToCloudEngine().connectCloudCompute();
        return null;
    });

    public EngineTest() {
        Project projectOrders = Project.builder().isForOrders(true).build().createObject();
        String parentFolder = AuthorizerSteps.getParentProject(projectOrders.getId());
        projectId = ((Project) Project.builder().projectName("Проект для EngineTest").folderName(parentFolder).build()
                .createObjectPrivateAccess()).getId();
    }

    @Override
    protected String getProjectId() {
        return projectId;
    }

    @BeforeAll
    public static void beforeAll() {
    }

    private void checkElementsEngine(Condition condition) {
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
        engineSup.run();
        checkElementsEngine(Condition.visible);
    }

    @Test
    @Order(2)
    @TmsLink("953813")
    @DisplayName("T1 Cloud Engine. Отключить")
    void disconnectEngine() {
        engineSup.run();
        new IndexPage().disconnectCloudEngine();
        checkElementsEngine(Condition.not(Condition.visible));
        TypifiedElement.refreshPage();
        new IndexPage().goToCloudEngine().getBtnConnect().getButton().should(Condition.visible, Duration.ofMinutes(2));
    }
}
