package ui.cloud.pages.productCatalog.actions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.InputFile;
import ui.elements.Table;
import ui.elements.TypifiedElement;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ActionsListPage extends BaseListPage {
    private static final String NAME_COLUMN = "Код действия";
    private final SelenideElement copyAction = $x("//li[text() = 'Создать копию']");
    private final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");

    public ActionsListPage() {
        SelenideElement actionPageTitle = $x("//div[text() = 'Действия']");
        actionPageTitle.shouldBe(Condition.visible);
    }

    @Step("Создание действия")
    public ActionPage createAction() {
        TestUtils.scrollToTheTop();
        addNewObjectButton.click();
        return new ActionPage();
    }

    /**
     * Метод для проверки существования действия по колонке "Код действия"
     *
     * @param value знаение для поиска в колонке "Код действия"
     * @return true если действие существует, false если действие не существует.
     */
    @Step("Проверка существования действия {value}")
    public boolean isActionExist(String value) {
        Table table = new Table(NAME_COLUMN);
        while (nextPageButton.isEnabled()) {
            if (table.isColumnValueEquals(NAME_COLUMN, value)) {
                return true;
            } else {
                nextPageButton.scrollIntoView(TypifiedElement.scrollCenter).click();
            }
        }
        return false;
    }

    @Step("Копирование действия {name}")
    public ActionPage copyAction(String name) {
        openActionMenu(name);
        copyAction.click();
        new Alert().checkText("Копирование выполнено успешно").checkColor(Alert.Color.GREEN);
        return new ActionPage();
    }

    @Step("Удаление действия {name}")
    public DeleteDialog deleteAction(String name) {
        openActionMenu(name);
        deleteAction.click();
        return new DeleteDialog();
    }

    @Step("Открытие формы действия {name}")
    public ActionPage openActionForm(String name) {
        new Table(NAME_COLUMN).getRowElementByColumnValue(NAME_COLUMN, name).click();
        TestUtils.wait(2000);
        return new ActionPage();
    }

    @Step("Открытие формы действия по строке {number}")
    public ActionPage openActionFormByRowNumber(int number) {
        new Table(NAME_COLUMN).getValueByColumnInRow(number, NAME_COLUMN).click();
        TestUtils.wait(2000);
        return new ActionPage();
    }

    @Step("Импорт действия из файла")
    public ActionsListPage importAction(String path) {
        importButton.scrollIntoView(TypifiedElement.scrollCenter).click();
        new InputFile(path).importFileAndSubmit();
        new Alert().checkText("Импорт выполнен успешно").checkColor(Alert.Color.GREEN).close();
        return this;
    }

    @Step("Переход на следующую страницу списка действий")
    public ActionsListPage goToNextPageActionList() {
        String firstActionName = new Table("Наименование").getFirstValueByColumn("Наименование");
        TestUtils.scrollToTheBottom();
        nextPageButton.click();
        String secondActionName = new Table("Наименование").getFirstValueByColumn("Наименование");
        assertNotEquals(firstActionName, secondActionName);
        return this;
    }

    @Step("Проверяем, что строка отличается визуально")
    public ActionsListPage checkActionIsHighlighted(int rowNumber) {
        Table actionList = new Table(NAME_COLUMN);
        Assertions.assertTrue(actionList.getRowByIndex(rowNumber)
                .getCssValue("color").contains("196, 202, 212"));
        return this;
    }


    private void openActionMenu(String name) {
        $x("//td[text() = '" + name + "']//parent::tr//button[@id = 'actions-menu-button']").click();
    }
}
