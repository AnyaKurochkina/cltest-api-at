package ui.t1.tests.cdn;

import core.enums.Role;
import core.helper.Report;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import models.AbstractEntity;
import models.t1.cdn.Resource;
import models.t1.cdn.SourceGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import steps.t1.cdn.CdnOriginGroupsClient;
import ui.elements.Alert;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.tests.AbstractT1Test;
import ui.t1.tests.WithAuthorization;
import ui.t1.tests.engine.EntitySupplier;

import java.util.Collections;

@Epic("CDN")
@Feature("Действия с группами источников")
@Tags({@Tag("cdn")})
@WithAuthorization(Role.CLOUD_ADMIN)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("morozov_ilya")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CdnSourceGroupTests extends AbstractT1Test {

    private final EntitySupplier<SourceGroup> cdnSourceGroup = lazy(() -> {
        SourceGroup sourceGroup = SourceGroup.builder()
                .projectId(getProjectId())
                .domainName("t1.ru")
                .name(RandomStringUtils.randomAlphabetic(4).toLowerCase() + ".autotest-source-group.com")
                .build().deleteMode(AbstractEntity.Mode.AFTER_CLASS);
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .create(sourceGroup);
        Alert.green("Группа источников успешно добавлена");
        return sourceGroup;
    });

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
    @DisplayName("CDN. Создание группы источников")
    @TmsLink("SOUL-5371")
    public void createSourceGroupTest() {
        String name = cdnSourceGroup.get().getName();
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .checkCdnEntityExistByName(name);
    }

    @Test
    @Order(2)
    @DisplayName("CDN. Редактирование группы источников")
    @TmsLink("SOUL-5372")
    public void editSourceGroupTest() {
        String newDomainName = "ya.ru";
        String name = cdnSourceGroup.get().getName();
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .editSourceGroupDomainName(name, newDomainName)
                .checkCdnSourceGroupContainsDomainName(name, newDomainName);
    }

    @Test
    @Order(3)
    @DisplayName("CDN. Невозможность добавить группу источников без \"Основного\" приоритета")
    @TmsLink("SOUL-5379")
    public void createSourceGroupAsReservedTest() {
        SourceGroup sourceGroup = SourceGroup.builder()
                .projectId(getProjectId())
                .domainName("t1.ru")
                .name(RandomStringUtils.randomAlphabetic(4).toLowerCase() + ".autotest-source-group.com")
                .isReserved(true)
                .build().deleteMode(AbstractEntity.Mode.AFTER_CLASS);

        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .create(sourceGroup);
        Alert.red("You cannot specify all origin sources as reserve (backup). Add non-reserve origins.");
    }

    @Test
    @Order(4)
    @DisplayName("CDN. Невозможность добавить группу источников с существующим в таблице названием")
    @TmsLink("SOUL-5378")
    public void createSourceGroupWithSameNameTest() {
        SourceGroup sourceGroup = cdnSourceGroup.get();
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .create(sourceGroup);
        Report.checkStep("Отображается нотификация с текстом - Origin Group name should be unique", () -> {
            Alert.red("Origin Group name should be unique");
        });
    }

    @Test
    @Order(5)
    @DisplayName("CDN. Невозможность удалить группу источников, которая используется в ресурсе")
    @TmsLinks(@TmsLink("SOUL-5380"))
    public void usedSourceGroupUnableToDeleteTest() {
        String hostName = cdnResource.get().getName();
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .delete(hostName);
        Alert.red("There are resources using such group");
    }

    @Test
    @Order(6)
    @DisplayName("CDN. Получение списка групп источников")
    @TmsLink("SOUL-5370")
    public void checkCounterTest() {
        cdnSourceGroup.get();
        String expectedCountOfSourceGroups = String.valueOf(CdnOriginGroupsClient.getSourceGroups(getProjectId()).size());
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .checkCounter(expectedCountOfSourceGroups);
    }

    @Test
    @Order(100)
    @DisplayName("CDN. Удаление группы источников")
    @TmsLink("SOUL-5373")
    public void deleteSourceGroupTest() {
        String name = cdnSourceGroup.get().getName();
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .delete(name)
                .checkThatCdnEntityDoesNotExist(name);
    }
}
