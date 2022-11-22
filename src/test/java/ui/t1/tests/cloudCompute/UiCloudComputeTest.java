package ui.t1.tests.cloudCompute;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.LoginPage;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudCompute.DiskCreatePage;
import ui.t1.pages.cloudCompute.DiskPage;
import ui.t1.pages.cloudCompute.DisksPage;
import ui.t1.pages.cloudCompute.SelectBox;

@ExtendWith(ConfigExtension.class)
@Epic("Cloud Compute")
@Tags({@Tag("ui_cloud_compute")})
@Log4j2
public class UiCloudComputeTest extends Tests {
    Project project;

    public UiCloudComputeTest() {
//        Project project = Project.builder().isForOrders(true).build().createObject();
//        String parentFolder = AuthorizerSteps.getParentProject(project.getId());
//        this.project = Project.builder().projectName("Проект для тестов Cloud Compute").folderName(parentFolder).build().createObjectPrivateAccess();
        this.project = Project.builder().id("proj-6opt7sq1fg").build();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    void name() {
        DiskCreatePage disk = new IndexPage()
                .goDisks()
                .addVm()
                .setMarketPlaceImage(new SelectBox.Image("Ubuntu", "20.04"))
                .setSize("5")
                .setType("SSD")
                .setName("Test-AT")
                .clickOrder();

        DiskPage diskPage = new DisksPage().selectDisk(disk.getName());


        System.out.println(1);
    }
}
