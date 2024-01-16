package ui.t1.tests.cdn;

import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.t1.cdn.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cdn.CdnPage;
import ui.t1.tests.AbstractT1Test;
import ui.t1.tests.WithAuthorization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.t1.CdnSteps.createResource;
import static steps.t1.CdnSteps.deleteSourceGroup;

@Epic("IAM и Управление")
@Feature("Действия с проектом")
@Tags({@Tag("ui_cloud_folder_actions")})
@Log4j2
@WithAuthorization(Role.CLOUD_ADMIN)
@ExtendWith(ConfigExtension.class)
public class CdnTest extends AbstractT1Test {

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
        List<String> hostNames = new ArrayList<>();
        hostNames.add(resourceName);
        Resource resource = new Resource("mirror.yandex.ru", hostNames);
        createResource(getProjectId(), resource.toJson());
        new IndexPage()
                .goToCdn()
                .waitChangeStatus(resourceName);
        resource.setActive(false);
        resource.setHostType("client");
        resource.setOriginProtocol("HTTPS");
        hostNames.add("mail.ru");
        hostNames.add("vk.ru");
        resource.setHostnames(resource.getHostnames());
        new CdnPage()
                .goToResourcePage(resourceName)
                .editResource(resource);

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
        deleteSourceGroup(getProjectId(), name);
    }
}
