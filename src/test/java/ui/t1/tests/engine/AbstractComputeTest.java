package ui.t1.tests.engine;

import api.Tests;
import com.codeborne.selenide.Selenide;
import com.mifmif.common.regex.Generex;
import core.enums.Role;
import core.helper.Configure;
import io.qameta.allure.Epic;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.BeforeAll;
import ru.testit.annotations.Title;
import ui.cloud.pages.LoginPage;
import ui.elements.TypifiedElement;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.compute.SelectBox;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@ExtendWith(ConfigExtension.class)
@Epic("Cloud Compute")
@Tags({@Tag("t1_ui_cloud_compute")})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractComputeTest extends Tests {
    protected Project project;
    protected String availabilityZone = "ru-central1-a";
    protected SelectBox.Image image = new SelectBox.Image("Ubuntu", "20.04");
    protected String hddTypeFirst = "HDD";
    protected String hddTypeSecond = "HDD";
    protected String securityGroup = "default";
    protected String flavorName = "Intel";
    private final String entitiesPrefix = "AT-" + this.getClass().getSimpleName();
    protected String sshKey = getRandomName();
    protected List<String> createdIpList = new ArrayList<>();

    public AbstractComputeTest() {
        project = Project.builder().isForOrders(true).build().createObject();
        if(Configure.ENV.equals("t1ift"))
            availabilityZone = "ru-central1-c";
    }

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @BeforeAll
    public void beforeAll() {
        new IndexPage().goToSshKeys().addKey(sshKey, "root");
        IndexPage.go();
    }

    protected String getRandomName() {
        return new Generex(entitiesPrefix + "-[a-z]{6}").random();
    }

    private void deleteProduct(IProductT1Page<?> product) {
        try {
            product.delete();
        } catch (Throwable e) {
            TypifiedElement.refresh();
        }
    }

    @AfterAll
    public void afterAll() {
        beforeEach();
        new IndexPage().goToVirtualMachine().getVmList().stream()
                .filter(e -> e.startsWith(entitiesPrefix)).forEach(e -> deleteProduct(new IndexPage().goToVirtualMachine().selectCompute(e)));
        new IndexPage().goToDisks().getDiskList().stream()
                .filter(e -> e.startsWith(entitiesPrefix)).forEach(e -> deleteProduct(new IndexPage().goToDisks().selectDisk(e)));
        new IndexPage().goToSnapshots().geSnapshotList().stream()
                .filter(e -> e.startsWith(entitiesPrefix)).forEach(e -> deleteProduct(new IndexPage().goToSnapshots().selectSnapshot(e)));
        new IndexPage().goToImages().getImageList().stream()
                .filter(e -> e.startsWith(entitiesPrefix)).forEach(e -> deleteProduct(new IndexPage().goToImages().selectImage(e)));
        new IndexPage().goToSshKeys().getSshKeysList().stream()
                .filter(e -> e.startsWith(entitiesPrefix)).forEach(e -> new IndexPage().goToSshKeys().deleteKey(e));
        createdIpList.forEach(e -> deleteProduct(new IndexPage().goToPublicIps().selectIp(e)));
        Selenide.closeWebDriver();
    }
}
