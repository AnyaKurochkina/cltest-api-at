package ui.cloud.tests.productCatalog.product;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.product.ProductPage;
import ui.elements.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ProductSteps.partialUpdateProductByName;
import static steps.productCatalog.TagSteps.deleteTagByName;
import static ui.cloud.pages.productCatalog.EntityPage.CALCULATED_VERSION_TITLE;

@Feature("Редактирование продукта")
public class EditProductTest extends ProductBaseTest {

    private static final List<String> tagList = new ArrayList<>();

    @AfterAll
    public static void deleteTags() {
        for (String name : tagList) {
            deleteTagByName(name);
        }
    }

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
    @TmsLink("1064198")
    @DisplayName("Загрузка иконки")
    public void addIcon() {
        partialUpdateProductByName(product.getName(), new JSONObject().put("icon_store_id", JSONObject.NULL)
                .put("icon_url", JSONObject.NULL));
        new ControlPanelIndexPage()
                .goToProductsListPage()
                .findAndOpenProductPage(product.getName());
        ProductPage page = new ProductPage();
        page.getIconInput().getInput().uploadFile(new File("src/test/resources/json/productCatalog/products/importProduct.json"));
        page.getIncorrectIconFormatHint().shouldBe(Condition.visible);
        page.getIconInput().getInput().uploadFile(new File("src/test/resources/icons/largeImage.jpg"));
        page.getIconTooLargeHint().shouldBe(Condition.visible);
        page.getIconInput().getInput().uploadFile(new File("src/test/resources/icons/svgIcon.svg"));
        page.saveWithoutPatchVersion(page.getSaveProductAlertText());
        page.getDeleteIconButton().shouldBe(Condition.visible);
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
        product.setGraphVersion(CALCULATED_VERSION_TITLE);
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
    @TmsLink("1071825")
    @DisplayName("Баннер при несохраненных изменениях")
    public void checkUnsavedChangesAlert() {
        new ControlPanelIndexPage().goToProductsListPage()
                .findAndOpenProductPage(NAME)
                .checkUnsavedChangesAlertAccept(product)
                .checkUnsavedChangesAlertDismiss();
    }

    @Test
    @TmsLink("SOUL-5047")
    @DisplayName("Добавить и удалить новый тег со страницы продукта")
    public void addAndDeleteNewTagOnPage() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        tagList.add(name);
        ProductPage page = new ControlPanelIndexPage().goToProductsListPage().findAndOpenProductPage(product.getName());
        page.addNewTag(name);
        page.saveWithoutPatchVersion(page.getSaveProductAlertText());
        page.deleteTag(name);
        page.saveWithoutPatchVersion(page.getSaveProductAlertText());
        assertTrue(new Table("Наименование").isEmpty());
    }
}
