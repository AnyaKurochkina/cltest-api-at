package ui.cloud.tests.productCatalog.product;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.product.ProductPage;

@Feature("Редактирование продукта")
public class EditProductTest extends ProductBaseTest {

    @Test
    @TmsLink("602293")
    @DisplayName("Проверка изменений и лимита патч-версий")
    public void checkPatchVersionLimit() {
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .checkVersion("1.0.0")
                .setAuthor("QA-1")
                .saveWithManualVersion("1.0.999")
                .checkVersion("1.0.999")
                .setAuthor("QA-2")
                .saveWithPatchVersion()
                .checkVersion("1.1.0")
                .setAuthor("QA-3")
                .saveWithManualVersion("1.999.999")
                .checkVersion("1.999.999")
                .setAuthor("QA-4")
                .saveWithPatchVersion()
                .checkVersion("2.0.0")
                .setAuthor("QA-5")
                .saveWithManualVersion("999.999.999")
                .goToMainTab()
                .checkVersionLimit();
    }

    @Test
    @TmsLink("602328")
    @DisplayName("Проверка изменений и лимита версий, указанных вручную")
    public void checkManualVersionLimit() {
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .checkVersion("1.0.0")
                .setAuthor("QA-1")
                .saveWithManualVersion("1.0.999")
                .checkVersion("1.0.999")
                .setAuthor("QA-2")
                .checkNextVersionAndSave("1.1.0")
                .checkVersion("1.1.0")
                .setAuthor("QA-3")
                .saveWithManualVersion("1.999.999")
                .checkVersion("1.999.999")
                .setAuthor("QA-4")
                .checkNextVersionAndSave("2.0.0")
                .checkVersion("2.0.0")
                .setAuthor("QA-5")
                .saveWithManualVersion("999.999.999")
                .goToMainTab()
                .checkVersionLimit();
    }

    @Test
    @TmsLink("631132")
    @DisplayName("Удаление иконки")
    public void deleteIconTest() {
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(product.getName())
                .deleteIcon();
    }

    @Test
    @TmsLink("507389")
    @DisplayName("Редактирование продукта")
    public void editProductTest() {
        product.setDescription("New description");
        product.setGraphVersion("Последняя");
        product.setIsOpen(true);
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(product.getName())
                .setAttributes(product)
                .saveWithPatchVersion();
        product.setVersion("1.0.1");
        new ProductPage().checkAttributes(product);
    }

    @Test
    @TmsLink("529386")
    @DisplayName("Проверка сохранения версии")
    public void saveWithManualVersionTest() {
        String currentServiceVersion = "1.0.1";
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
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
    @TmsLink("1071825")
    @DisplayName("Баннер при несохраненных изменениях")
    public void checkUnsavedChangesAlert() {
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .checkUnsavedChangesAlertAccept(product)
                .checkUnsavedChangesAlertDismiss();
    }
}
