package ui.cloud.tests.services;

import core.helper.Configure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.authorizer.Project;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import tests.Tests;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.LoginPage;
import ui.cloud.pages.services.SmokeTestPage;
import ui.uiExtesions.ConfigExtension;

@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Сервисы")
@Feature("Test services")
@Tags({@Tag("ui_services")})
@Log4j2
public class ServicesTest extends Tests {
    Project project;

    //TODO: пока так :)
    public ServicesTest() {
        if (Configure.ENV.equals("prod"))
            project = Project.builder().id("proj-ghz4hv6a2g").build();
        else
            project = Project.builder().id("proj-5f6ws2rr75").build();
    }

    @BeforeEach
    @Title("Авторизация на портале")
    void beforeEach() {
        new LoginPage(project.getId())
                .signIn();
    }

    @Test
    @DisplayName("Сервисы. Запуск Smoke TEST")
    void runSmokeTest() {
        SmokeTestPage smokeTestPage = new SmokeTestPage();
        new IndexPage().goToServicesListPage().selectProduct(smokeTestPage.getServiceName());
        smokeTestPage.run();
    }

}
