package ui.cloud.tests.productCatalog.service;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.productCatalog.service.ServicePage;

@Feature("Редактирование сервиса")
public class EditServiceTest extends ServiceBaseTest {

    @Test
    @TmsLink("504801")
    @DisplayName("Редактирование сервиса")
    public void editServiceTest() {
        service.setDescription("new description");
        service.setGraphVersion("Последняя");
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(service.getName())
                .setAttributes(service)
                .saveWithPatchVersion();
        service.setVersion("1.0.1");
        new ServicePage().checkAttributes(service);
    }

    @Test
    @TmsLink("529543")
    @DisplayName("Проверка сохранения версии")
    public void saveWithManualVersionTest() {
        String currentServiceVersion = "1.0.1";
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .setGraphVersion("Последняя")
                .saveWithPatchVersion()
                .setGraphVersion("1.0.0")
                .checkSaveWithInvalidVersion("1.0.0", currentServiceVersion)
                .checkSaveWithInvalidVersion("1.0.0", currentServiceVersion)
                .checkSaveWithInvalidVersionFormat("1/0/2")
                .saveWithManualVersion("1.0.2")
                .checkVersion("1.0.2");
    }

    @Test
    @TmsLink("602549")
    @DisplayName("Проверка изменений и лимита патч-версий")
    public void checkPatchVersionLimit() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .checkVersion("1.0.0")
                .setExtraData("{\"test_value\":1}")
                .saveWithManualVersion("1.0.999")
                .checkVersion("1.0.999")
                .setExtraData("{\"test_value\":2}")
                .saveWithPatchVersion()
                .checkVersion("1.1.0")
                .setExtraData("{\"test_value\":3}")
                .saveWithManualVersion("1.999.999")
                .checkVersion("1.999.999")
                .setExtraData("{\"test_value\":4}")
                .saveWithPatchVersion()
                .checkVersion("2.0.0")
                .setExtraData("{\"test_value\":5}")
                .saveWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("602552")
    @DisplayName("Проверка изменений и лимита версий, указанных вручную")
    public void checkManualVersionLimit() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .checkVersion("1.0.0")
                .setExtraData("{\"test_value\":1}")
                .saveWithManualVersion("1.0.999")
                .checkVersion("1.0.999")
                .setExtraData("{\"test_value\":2}")
                .checkNextVersionAndSave("1.1.0")
                .checkVersion("1.1.0")
                .setExtraData("{\"test_value\":3}")
                .saveWithManualVersion("1.999.999")
                .checkVersion("1.999.999")
                .setExtraData("{\"test_value\":4}")
                .checkNextVersionAndSave("2.0.0")
                .checkVersion("2.0.0")
                .setExtraData("{\"test_value\":5}")
                .saveWithManualVersion("999.999.999")
                .checkVersionLimit();
    }

    @Test
    @TmsLink("631151")
    @DisplayName("Удаление иконки")
    public void deleteIconTest() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(service.getName())
                .deleteIcon();
    }

    @Test
    @TmsLink("1071896")
    @DisplayName("Баннер при несохраненных изменениях")
    public void checkUnsavedChangesAlert() {
        new IndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .checkUnsavedChangesAlertAccept(service)
                .checkUnsavedChangesAlertDismiss();
    }
}
