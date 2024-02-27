package ui.cloud.tests.productCatalog.service;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.service.ServicePage;

import java.io.File;

import static steps.productCatalog.ServiceSteps.partialUpdateServiceById;
import static steps.productCatalog.ServiceSteps.partialUpdateServiceByName;
import static ui.cloud.pages.productCatalog.EntityPage.CALCULATED_VERSION_TITLE;

@Feature("Редактирование сервиса")
public class EditServiceTest extends ServiceBaseTest {

    @Test
    @TmsLink("504801")
    @DisplayName("Редактирование сервиса")
    public void editServiceTest() {
        service.setDescription("new description");
        service.setGraphVersion(CALCULATED_VERSION_TITLE);
        service.setIsPublished(true);
        new ControlPanelIndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(service.getName())
                .setAttributes(service)
                .saveWithPatchVersion();
        service.setVersion("1.0.1");
        new ServicePage().checkAttributes(service);
        partialUpdateServiceById(service.getId(), new JSONObject().put("is_published", false));
    }

    @Test
    @TmsLink("529543")
    @DisplayName("Проверка сохранения версии")
    public void saveWithManualVersionTest() {
        String currentServiceVersion = "1.0.1";
        new ControlPanelIndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .setGraphVersion(CALCULATED_VERSION_TITLE)
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
        new ControlPanelIndexPage().goToServicesListPagePC()
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
        new ControlPanelIndexPage().goToServicesListPagePC()
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
    @TmsLink("1130051")
    @DisplayName("Загрузка иконки")
    public void addIcon() {
        partialUpdateServiceByName(service.getName(), new JSONObject().put("icon_store_id", JSONObject.NULL)
                .put("icon_url", JSONObject.NULL));
        new ControlPanelIndexPage()
                .goToServicesListPagePC()
                .findAndOpenServicePage(service.getName());
        ServicePage page = new ServicePage();
        page.getIconInput().getInput().uploadFile(new File("src/test/resources/json/productCatalog/products/importProduct.json"));
        page.getIncorrectIconFormatHint().shouldBe(Condition.visible);
        page.getIconInput().getInput().uploadFile(new File("src/test/resources/icons/largeImage.jpg"));
        page.getIconTooLargeHint().shouldBe(Condition.visible);
        page.getIconInput().getInput().uploadFile(new File("src/test/resources/icons/svgIcon.svg"));
        page.saveWithoutPatchVersion(page.getSaveServiceAlertText());
        page.getDeleteIconButton().shouldBe(Condition.visible);
    }

    @Test
    @TmsLink("631151")
    @DisplayName("Удаление иконки")
    public void deleteIconTest() {
        new ControlPanelIndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(service.getName())
                .deleteIcon();
    }

    @Test
    @TmsLink("1071896")
    @DisplayName("Баннер при несохраненных изменениях")
    public void checkUnsavedChangesAlert() {
        new ControlPanelIndexPage().goToServicesListPagePC()
                .findAndOpenServicePage(NAME)
                .checkUnsavedChangesAlertAccept(service)
                .checkUnsavedChangesAlertDismiss();
    }
}
