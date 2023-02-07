package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Input;
import ui.elements.Select;

import static com.codeborne.selenide.Selenide.$x;

public class BasePage {

    protected final Select versionDropDown = Select.byLabel("Выберите версию");
    protected final WebElement versionComparisonTab = $x("//button[span[text()='Сравнение версий']]");
    protected final SelenideElement selectedVersion = $x("//label[text()='Выберите версию']/..//div[@id='selectValueWrapper']/div");
    protected final Button createButton = Button.byText("Создать");
    protected final Button saveButton = Button.byText("Сохранить");
    protected final Button cancelButton = Button.byText("Отмена");
    protected final Button deleteButton = Button.byText("Удалить");
    protected final Button backButton = Button.byText("Назад");
    protected final SelenideElement deleteIconButton = $x("//img/following::*[name()='svg'][1]");
    protected final SelenideElement addIconLabel = $x("//label[text()='Добавить иконку']");
    protected final Select graphSelect = Select.byLabel("Граф");
    protected final Select graphVersionSelect = Select.byLabel("Значение");
    protected final Button viewJSONButton = Button.byText("JSON");
    protected final Button expandJSONView = Button.byAriaLabel("fullscreen");
    protected final Button closeJSONView = Button.byAriaLabel("close");
    protected final Input nameInput = Input.byName("name");
    protected final Input titleInput = Input.byName("title");

    @Step("Сохранение объекта без изменения версии")
    public BasePage saveWithoutPatchVersion(String alertText) {
        saveButton.click();
        Alert.green(alertText);
        return this;
    }

    @Step("Сохранение объекта со следующей патч-версией")
    public BasePage saveWithPatchVersion(String alertText) {
        saveButton.click();
        new SaveDialog().saveWithNextPatchVersion(alertText);
        return this;
    }

    @Step("Сохранение объекта с версией '{newVersion}'")
    public BasePage saveWithManualVersion(String newVersion, String alertText) {
        saveButton.click();
        new SaveDialog().saveWithVersion(newVersion, alertText);
        return this;
    }

    @Step("Проверка, что следующая предлагаемая версия для сохранения равна '{nextVersion}' и сохранение")
    public BasePage checkNextVersionAndSave(String nextVersion, String alertText) {
        saveButton.click();
        new SaveDialog().checkNextVersionAndSave(nextVersion, alertText);
        return this;
    }

    @Step("Проверка недоступности сохранения объекта с некорректной версией '{newVersion}'")
    public BasePage checkSaveWithInvalidVersion(String newVersion, String currentVersion) {
        saveButton.click();
        new SaveDialog().checkSaveWithInvalidVersion(newVersion, currentVersion);
        return this;
    }

    @Step("Проверка недоступности сохранения объекта с версией некорректного формата '{newVersion}'")
    public BasePage checkSaveWithInvalidVersionFormat(String newVersion) {
        saveButton.click();
        new SaveDialog().checkSaveWithInvalidVersionFormat(newVersion);
        return this;
    }

    @Step("Проверка недоступности сохранения объекта при достижении лимита версий")
    public BasePage checkVersionLimit() {
        $x("//div[text()='Достигнут предел допустимого значения версии. Нельзя сохранить следующую версию']")
                .shouldBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.disabled);
        return this;
    }

    @Step("Переход на вкладку '{title}'")
    public void goToTab(String title) {
        SelenideElement tab = $x("//button[span[text()='" + title + "']]");
        if (tab.getAttribute("aria-selected").equals("false")) {
            tab.scrollIntoView(false).click();
        }
    }

    @Step("Проверка, что открыта вкладка '{title}'")
    public void checkTabIsSelected(String title) {
        SelenideElement tab = $x("//button[span[text()='" + title + "']]");
        Assertions.assertTrue(Boolean.valueOf(tab.getAttribute("aria-selected")), "Вкладка " + title + " не выбрана");
    }

    @Step("Просмотр JSON и проверка отображения '{value}'")
    public BasePage checkJSONcontains(String value) {
        viewJSONButton.click();
        $x("//div[@role='dialog']//span[text()='\"" + value + "\"']").shouldBe(Condition.visible);
        expandJSONView.click();
        expandJSONView.click();
        closeJSONView.click();
        return this;
    }
}
