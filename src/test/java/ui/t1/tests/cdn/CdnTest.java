package ui.t1.tests.cdn;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.t1.CdnSteps.createResource;
import static steps.t1.CdnSteps.deleteSourceGroup;

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
    @DisplayName("CDN. Создание/Удаление ресурса. Удаление группы ресурсов.")
    @TmsLinks({@TmsLink("SOUL-5366"), @TmsLink("SOUL-5369"), @TmsLink("SOUL-5373")})
    public void createThenDeleteCdnResourceTest() {
        Resource resource = new Resource("mirror.yandex.ru", Collections.singletonList("createresource.ya.ru"));
        new IndexPage()
                .goToCdn()
                .createResource(resource);
        assertTrue(new CdnPage().isEntityExist(resource.getHostnames().get(0)));
        assertFalse(new CdnPage().deleteResource(resource.getHostnames().get(0)));
        assertFalse(new CdnPage().deleteSourceGroup(resource.getHostnames().get(0)));
    }

    @Test
    @DisplayName("CDN. Редактирование ресурса.")
    @TmsLink("")
    public void editResourceTest() {
        String resourceName = "editresource.ya.ru";
        Resource resource = new Resource("mirror.yandex.ru", Collections.singletonList("editresource.ya.ru"));
        createResource(project.getId(), resource.toJson());
        new IndexPage()
                .goToCdn()
                .waitChangeStatus(resourceName);

    }

    @Test
    @DisplayName("CDN. Создание группы источников.")
    @TmsLink("SOUL-5371")
    public void deleteSourceGroupTest() {
        String name = "create.source.group";
        new IndexPage()
                .goToCdn()
                .createSourceGroup(name, "t1.ru");
        assertTrue(new CdnPage().isEntityExist(name));
        deleteSourceGroup(project.getId(), name);
    }
}
