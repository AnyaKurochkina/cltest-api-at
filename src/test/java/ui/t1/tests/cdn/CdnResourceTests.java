package ui.t1.tests.cdn;

import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.AbstractEntity;
import models.t1.cdn.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.elements.Alert;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.tests.AbstractT1Test;
import ui.t1.tests.WithAuthorization;
import ui.t1.tests.engine.EntitySupplier;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Epic("CDN")
@Feature("Действия с ресурсами")
@Tags({@Tag("cdn")})
@WithAuthorization(Role.CLOUD_ADMIN)
@ExtendWith(ConfigExtension.class)
@Tag("morozov_ilya")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CdnResourceTests extends AbstractT1Test {

    private final EntitySupplier<Resource> cdnResource = lazy(() -> {
        Resource resource = new Resource(getProjectId(), "mirror.yandex.ru",
                Collections.singletonList(RandomStringUtils.randomAlphabetic(8).toLowerCase() + ".ya.ru"))
                .deleteMode(AbstractEntity.Mode.AFTER_CLASS);
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .create(resource);
        return resource;
    });

    @Test
    @Order(1)
    @DisplayName("CDN. Создание ресурса.")
    @TmsLinks(@TmsLink("SOUL-5366"))
    public void createCdnResourceTest() {
        String hostName = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .checkCdnEntityExistByName(hostName);
    }

    @Test
    @Order(2)
    @DisplayName("CDN. Редактирование ресурса.")
    @TmsLink("SOUL-5367")
    public void editResourceTest() {
        List<String> domains = Stream
                .generate(() -> RandomStringUtils.randomAlphabetic(4).toLowerCase() + ".autotest.com")
                .limit(2)
                .collect(Collectors.toList());
        String name = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .goToResourcePage(name)
                .editResourceHostNames(domains)
                .checkDomainsColumnHasNames(domains);
    }

    @Test
    @Order(3)
    @DisplayName("CDN. Очистка кэша. Полная")
    @TmsLink("SOUL-5383")
    public void resetFullCacheTest() {
        String name = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .goToResourcePage(name)
                .fullResetCache();
        Alert.green("Кэш очищен");
    }

    @Test
    @Order(4)
    @DisplayName("CDN. Очистка кэша. Выборочная")
    @TmsLink("SOUL-5377")
    public void resetPartialCacheTest() {
        String name = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .goToResourcePage(name)
                .partialResetCache();
        Alert.green("Кэш очищен");
    }

    @Test
    @Order(100)
    @DisplayName("CDN. Удаление ресурса")
    @TmsLinks(@TmsLink("SOUL-5369"))
    public void deleteCdnResourceTest() {
        String name = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .delete(name)
                .checkThatCdnEntityDoesNotExist(name);
    }
}
