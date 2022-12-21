package ui.t1.tests.cloudCompute;

import com.mifmif.common.regex.Generex;
import core.enums.Role;
import io.qameta.allure.Epic;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.BeforeAll;
import ru.testit.annotations.Title;
import ui.cloud.pages.LoginPage;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudCompute.BeforeAllExtension;
import ui.t1.pages.cloudCompute.SelectBox;

@Log4j2
@ExtendWith(ConfigExtension.class)
@ExtendWith(BeforeAllExtension.class)
@Epic("Cloud Compute")
@Tags({@Tag("ui_cloud_compute")})
public abstract class AbstractComputeTest {
    Project project;
    String availabilityZone = "ru-central1-a";
    SelectBox.Image image = new SelectBox.Image("Ubuntu", "20.04");
    String hddTypeOne = "HDD";
    String hddTypeSecond = "HDD";
    String securityGroup = "default";
    String sshKey = "default";

    public AbstractComputeTest() {
//        Project project = Project.builder().isForOrders(true).build().createObject();
//        String parentFolder = AuthorizerSteps.getParentProject(project.getId());
//        this.project = Project.builder().projectName("Проект для тестов Cloud Compute").folderName(parentFolder).build().createObjectPrivateAccess();
        this.project = Project.builder().id("proj-votmndlfyh").build();
//        this.project = Project.builder().id("proj-2cdvptgjx7").build();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @BeforeAll
    public void beforeAll() {
        new IndexPage().goToSshKeys().addKey(sshKey, "root");
        IndexPage.go();
    }

    protected String getRandomName(){
        return new Generex("AT-UI-[a-z]{6}").random();
    }
}
