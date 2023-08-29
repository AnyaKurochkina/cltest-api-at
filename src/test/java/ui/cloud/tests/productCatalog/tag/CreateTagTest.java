package ui.cloud.tests.productCatalog.tag;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.tag.TagsListPage;
import ui.elements.Alert;
import ui.elements.Dialog;

import static steps.productCatalog.TagSteps.deleteTagByName;

@Feature("Создание тега")
public class CreateTagTest extends TagTest {

    @Test
    @Disabled("До исправления PO-1310")
    @TmsLink("SOUL-1060")
    @DisplayName("Добавление тега")
    public void createTag() {
        String name = "qa_at_tag_" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        TagsListPage page = new ControlPanelIndexPage().goToTagsPage();
        page.getAddNewObjectButton().click();
        Dialog dialog = new Dialog("Добавление нового тега");
        dialog.setInputValue("Наименование", name).clickButton("Добавить");
        Alert.green("Тег создан");
        page.getAddNewObjectButton().click();
        dialog.setInputValue("Наименование", name);
        page.getNonUniqueNameHint().shouldBe(Condition.visible);
        dialog.setInputValue("Наименование", " ");
        page.getEmptyNameHint().shouldBe(Condition.visible);
        deleteTagByName(name);
    }
}
