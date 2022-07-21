package ui.cloud.pages.productCatalog.actions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.Alert;
import ui.elements.DeleteDialog;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;

public class ActionsListPage {
    private static final String NAME_COLUMN = "Код действия";
    private final SelenideElement createButton = $x("//*[@title= 'Создать']");
    private final SelenideElement copyAction = $x("//li[text() = 'Создать копию']");
    private final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");

    public ActionsListPage() {
        SelenideElement actionPageTitle = $x("//div[text() = 'Действия']");
        actionPageTitle.shouldBe(Condition.visible);
    }

    @Step("Создание действия")
    public ActionPage createAction() {
        createButton.click();
        return new ActionPage();
    }

    /**
     * Метод для проверки существования действия по колонке "Код действия"
     *
     * @param value знаение для поиска в колонке "Код действия"
     * @return true если действие существует, false если действие не существует.
     */
    public boolean isActionExist(String value) {
        return new Table(NAME_COLUMN).isColumnValueExist(NAME_COLUMN, value);
    }

    @Step("Копирование действия")
    public ActionPage copyAction(String name) {
        openActionMenu(name);
        copyAction.click();
        new Alert().checkText("Копирование выполнено успешно").checkColor(Alert.Color.GREEN);
        return new ActionPage();
    }

    @Step("Удаление действия")
    public DeleteDialog deleteAction(String name) {
        openActionMenu(name);
        deleteAction.click();
        return new DeleteDialog();
    }

    @Step("Открытие формы действия")
    public ActionPage openActionForm(String name) {
        new Table(NAME_COLUMN).getRowElementByColumnValue(NAME_COLUMN, name).click();
        return new ActionPage();
    }


    private void openActionMenu(String name) {
        $x("//td[text() = '" + name + "']//parent::tr//button[@id = 'actions-menu-button']").click();
    }
}
