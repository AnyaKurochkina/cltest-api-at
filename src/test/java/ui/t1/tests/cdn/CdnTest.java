package ui.t1.tests.cdn;

import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import models.AbstractEntity;
import models.t1.cdn.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cdn.CdnPage;
import ui.t1.tests.AbstractT1Test;
import ui.t1.tests.WithAuthorization;
import ui.t1.tests.engine.EntitySupplier;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static steps.t1.cdn.CdnOriginGroupsClient.deleteSourceGroupByName;

@Epic("IAM и Управление")
@Feature("Действия с проектом")
@Tags({@Tag("ui_cloud_folder_actions")})
@Log4j2
@WithAuthorization(Role.CLOUD_ADMIN)
@ExtendWith(ConfigExtension.class)
public class CdnTest extends AbstractT1Test {

    protected final EntitySupplier<Resource> cdn = lazy(() -> {
        Resource resource = new Resource(getProjectId(), "mirror.yandex.ru",
                Collections.singletonList("my.test.com")).deleteMode(AbstractEntity.Mode.AFTER_CLASS);
        new IndexPage().goToCdn()
                .createResource(resource);
        return resource;
    });

    @Test
    @Order(1)
    @DisplayName("CDN. Создание/Удаление ресурса. Удаление группы ресурсов.")
    @TmsLinks({@TmsLink("SOUL-5366"), @TmsLink("SOUL-5369"), @TmsLink("SOUL-5373")})
    public void createCdnResourceTest() {
        String hostName = cdn.get().getName();
        new IndexPage().goToCdn()
                .checkCdnResourceExistByName(hostName);
    }

    @Test
    @Order(2)
    @DisplayName("CDN. Редактирование ресурса.")
    @TmsLink("")
    public void editResourceTest() {
        List<String> domains = Stream
                .generate(() -> RandomStringUtils.randomAlphabetic(6).toLowerCase() + ".ru")
                .limit(2)
                .collect(Collectors.toList());
        Resource resource = cdn.get();
        CdnPage cdnPage = new IndexPage().goToCdn()
                .waitChangeStatus(resource.getName());
        cdnPage.goToResourcePage(resource.getName())
                .editResourceHostNames(domains)
                .checkDomainsColumnHasNames(domains);
    }

    @Test
    @Order(3)
    @DisplayName("CDN. Создание группы источников.")
    @TmsLink("SOUL-5371")
    public void deleteSourceGroupTest() {
        String name = "create.source.group";
        new IndexPage().goToCdn()
                .createSourceGroup(name, "t1.ru");
        new CdnPage().checkCdnResourceExistByName(name);
        deleteSourceGroupByName(getProjectId(), name);
    }
}
