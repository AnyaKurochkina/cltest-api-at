package ui.t1.tests.cdn;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import models.t1.cdn.Resource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.T1LoginPage;
import ui.t1.pages.cdn.CdnPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("IAM и Управление")
@Feature("Действия с проектом")
@Tags({@Tag("ui_cloud_folder_actions")})
@Log4j2
@ExtendWith(ConfigExtension.class)
public class CdnTest extends Tests {
    Project project = Project.builder().isForOrders(true).build().createObject();

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new T1LoginPage(project.getId())
                .signIn(Role.CLOUD_ADMIN);
    }

    @Test
    @DisplayName("CDN. Создание ресурса.")
    public void createCdnResourceTest() {
        Resource resource = new Resource("mirror.yandex.ru", "createresource.ya.ru");
        new IndexPage()
                .goToCdn()
                .createResource(resource);
        assertTrue(new CdnPage().isResourceExist(resource.getHostName()));
    }
}
