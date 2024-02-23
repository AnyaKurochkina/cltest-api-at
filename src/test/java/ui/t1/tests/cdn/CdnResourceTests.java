package ui.t1.tests.cdn;

import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.AbstractEntity;
import models.t1.cdn.Resource;
import models.t1.cdn.SourceGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.t1.cdn.CdnResourceClient;
import ui.elements.Alert;
import ui.elements.TypifiedElement;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cdn.ResourcesTab;
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

    private final static String DEFAULT_LOCATION = "Russia / Moscow-EC / M9 | M9P";

    private final EntitySupplier<Resource> cdnResource = lazy(() -> {
        Resource resource = new Resource(getProjectId(), "mirror.yandex.ru",
                Collections.singletonList(RandomStringUtils.randomAlphabetic(8).toLowerCase() + ".ya.ru"))
                .deleteMode(AbstractEntity.Mode.AFTER_CLASS);
        SourceGroup.builder()
                .projectId(getProjectId())
                .domainName(resource.getDomainName())
                .name(resource.getName())
                .build()
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
                .switchToResourceGeneralTab(name)
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
                .switchToResourceGeneralTab(name)
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
                .switchToResourceGeneralTab(name)
                .partialResetCache();
        Alert.green("Кэш очищен");
    }

    @Test
    @Order(5)
    @DisplayName("CDN. HTTP -заголовки и методы")
    @TmsLinks(@TmsLink("SOUL-5386"))
    public void httpsHeadersTest() {
        String name = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .checkCdnEntityExistByName(name)
                .goToResourcePage(name)
                .switchToHttpHeadersTab()
                .checkTitles()
                .clickEditButton()
                .checkEditModalIsAppear();
    }

    @Test
    @Order(6)
    @DisplayName("CDN.Экранирование источников. Подключение")
    @TmsLinks(@TmsLink("SOUL-5388"))
    public void shieldingSourcesConnectionTest() {
        String name = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .checkCdnEntityExistByName(name)
                .goToResourcePage(name)
                .switchToShieldingOfSources()
                .activateShieldingWithLocation(DEFAULT_LOCATION)
                .checkShieldingIsActivatedWithLocation(DEFAULT_LOCATION);
    }

    @Test
    @Order(7)
    @DisplayName("CDN.Экранирование источников. Редактирование")
    @TmsLinks(@TmsLink("SOUL-5391"))
    @DisabledIfEnv("t1prod")
    public void shieldingSourcesEditTest() {
        String name = cdnResource.get().getName();
        String newLocation = "Singapore / Singapore-EC / SG1-EC";
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .checkCdnEntityExistByName(name)
                .goToResourcePage(name)
                .switchToShieldingOfSources()
                .activateShieldingWithLocation(DEFAULT_LOCATION)
                .changeLocation(newLocation)
                .checkShieldingIsActivatedWithLocation(newLocation);
    }

    @Test
    @Order(8)
    @DisplayName("CDN.Экранирование источников. Выключение")
    @TmsLinks(@TmsLink("SOUL-5393"))
    public void shieldingSourcesOffTest() {
        String name = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .checkCdnEntityExistByName(name)
                .goToResourcePage(name)
                .switchToShieldingOfSources()
                .activateShieldingWithLocation(DEFAULT_LOCATION)
                .checkShieldingIsActivatedWithLocation(DEFAULT_LOCATION)
                .offShielding()
                .checkThatShieldingIsOff();
    }

    @Test
    @Order(9)
    @DisplayName("CDN.Пресет \"LIVE STREAMING\". Включение.")
    @TmsLinks(@TmsLink("SOUL-5390"))
    public void enableLiveStreamingTest() {
        String name = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .checkCdnEntityExistByName(name)
                .enableLiveStreaming(name)
                .checkLiveStreamingIsEnabled(name);
    }

    @Test
    @Order(10)
    @DisplayName("CDN.Пресет \"LIVE STREAMING\". Выключение.")
    @TmsLinks(@TmsLink("SOUL-5390"))
    public void disableLiveStreamingTest() {
        String name = cdnResource.get().getName();
        ResourcesTab resourcesTab = new IndexPage().goToCdn()
                .switchToResourceTab()
                .checkCdnEntityExistByName(name);

        CdnResourceClient.enableLiveStreamingByName(getProjectId(), name);
        TypifiedElement.refreshPage();
        resourcesTab.disableLiveStreaming(name)
                .checkLiveStreamingIsDisabled(name);
    }

    @Test
    @Order(11)
    @DisplayName("CDN. Получение списка ресурсов")
    @TmsLink("SOUL-5368")
    public void checkCounterTest() {
        cdnResource.get();
        String expectedCountOfSourceGroups = String.valueOf(CdnResourceClient.getResources(getProjectId()).size());
        new IndexPage().goToCdn()
                .switchToResourceTab()
                .checkCounter(expectedCountOfSourceGroups);
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
