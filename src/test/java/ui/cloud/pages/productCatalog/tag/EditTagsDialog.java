package ui.cloud.pages.productCatalog.tag;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.SearchSelect;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;

public class EditTagsDialog extends Dialog {

    private final Button addButton = Button.byText("Добавить");
    private final Button closeButton = Button.byText("Закрыть");
    private final SearchSelect tagSelect = SearchSelect.byXpath("//div[@role='dialog']//div[select]");
    private final SelenideElement saveTagIcon = $x("//div[@role='dialog']//*[name()='svg'][.='Сохранить']");

    public EditTagsDialog() {
        super("Редактировать теги");
        this.getDialog().shouldBe(Condition.visible.because("Должен отображаться диалог редактирования тегов"));
    }

    @Step("Добавление тега '{name}'")
    public EditTagsDialog addTag(String name) {
        addButton.click();
        tagSelect.set(name);
        saveTagIcon.shouldBe(Condition.visible.because("Должна отображаться иконка сохранения тега")).click();
        return this;
    }

    @Step("Закрытие диалога редактирования тегов")
    public EntityListPage closeDialog() {
        closeButton.click();
        return new EntityListPage();
    }

    @Step("Удаление тега '{name}'")
    public EditTagsDialog removeTag(String name) {
        Table tagsTable = new Table($x("//div[@role='dialog']//table"));
        tagsTable.getRowByColumnValue("Наименование", name).get()
                .$x(".//*[name()='svg']").shouldBe(Condition.visible.because("Должна отображаться иконка удаления тега"))
                .click();
        new DeleteDialog().submitAndDelete();
        return this;
    }
}
