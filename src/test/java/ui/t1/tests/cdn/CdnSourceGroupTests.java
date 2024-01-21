package ui.t1.tests.cdn;

import core.enums.Role;
import core.helper.Report;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import models.t1.cdn.SourceGroup;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.elements.Alert;
import ui.extesions.ConfigExtension;
import ui.t1.pages.IndexPage;
import ui.t1.tests.AbstractT1Test;
import ui.t1.tests.WithAuthorization;
import ui.t1.tests.engine.EntitySupplier;

@Epic("CDN")
@Feature("Действия с группами источников")
@Tags({@Tag("cdn")})
@WithAuthorization(Role.CLOUD_ADMIN)
@ExtendWith(ConfigExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CdnSourceGroupTests extends AbstractT1Test {

    private final EntitySupplier<SourceGroup> cdnSourceGroup = lazy(() -> {
        SourceGroup sourceGroup = new SourceGroup(getProjectId(), "t1.ru",
                RandomStringUtils.randomAlphabetic(4).toLowerCase() + ".autotest-source-group.com")
                .deleteMode(AbstractEntity.Mode.AFTER_CLASS);
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .createSourceGroup(sourceGroup);
        Alert.green("Группа источников успешно добавлена");
        return sourceGroup;
    });

    @Test
    @Order(1)
    @DisplayName("CDN. Создание группы источников")
    @TmsLink("SOUL-5371")
    public void createSourceGroupTest() {
        String name = cdnSourceGroup.get().getName();
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .checkCdnSourceGroupExistByName(name);
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
    @DisplayName("CDN. Невозможность добавить группу источников с существующим в таблице названием")
    @TmsLink("SOUL-5378")
    public void createSourceGroupWithSameNameTest() {
        SourceGroup sourceGroup = cdnSourceGroup.get();
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .createSourceGroup(sourceGroup);
        Report.checkStep("Отображается нотификация с текстом - Origin Group name should be unique", () -> {
            Alert.red("Origin Group name should be unique");
        });
    }

    @Test
    @Order(100)
    @DisplayName("CDN. Удаление группы источников")
    @TmsLink("SOUL-5373")
    public void deleteSourceGroupTest() {
        String name = cdnSourceGroup.get().getName();
        new IndexPage().goToCdn()
                .switchToSourceGroupTab()
                .deleteSourceGroup(name)
                .checkCdnSourceGroupDoesNotExistByName(name);
    }
}
