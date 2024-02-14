package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;
import ui.elements.*;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.switchTo;

@Getter
public class EntityPage {

    public static final String CALCULATED_VERSION_TITLE = "Вычисляемая";
    protected final Select versionSelect = Select.byLabel("Выберите версию");
    protected final SelenideElement selectedVersion =
            $x("//label[text()='Выберите версию']/..//div[@id='selectValueWrapper']/div");
    protected final Button createButton = Button.byText("Создать");
    protected final Button saveButton = Button.byText("Сохранить");
    protected final Button cancelButton = Button.byText("Отмена");
    protected final Button deleteButton = Button.byText("Удалить");
    protected final Button backButton = Button.byText("Назад");
    protected final SelenideElement deleteIconButton = $x("//form//img/following::*[name()='svg'][1]");
    protected final SelenideElement addIconLabel = $x("//label[text()='Добавить иконку']");
    protected final SearchSelect graphSelect = SearchSelect.byLabel("Граф");
    protected final Select graphVersionSelect = Select.byLabel("Значение");
    protected final Button viewJSONButton = Button.byText("JSON");
    protected final Button expandJSONView = Button.byAriaLabel("fullscreen");
    protected final Button closeJSONView = Button.byAriaLabel("close");
    protected final Input nameInput = Input.byName("name");
    protected final Input titleInput = Input.byName("title");
    protected final SelenideElement mainPage = $x("//a[@href='/meccano/home']");
    protected final String unsavedChangesAlertText = "Внесенные изменения не сохранятся. Покинуть страницу?";
    protected final SelenideElement objectInfoEditor = $x("//div[@class='jodit-wysiwyg']");
    private final Button addTagButton = Button.byText("Добавить");
    private final Tab versionComparisonTab = Tab.byText("Сравнение");
    private final Tab restrictionsTab = Tab.byText("Ограничения");
    private final Tab tagsTab = Tab.byText("Теги");
    private final Input iconInput = Input.byXpath("//input[@type='file'][@accept='image/*']");
    private final SelenideElement incorrectIconFormatHint = iconInput.getInput()
            .$x("following::div[text()='Формат файла не поддерживается. Поддерживаемые форматы: png, jpeg, svg']");
    private final SelenideElement iconTooLargeHint = iconInput.getInput()
            .$x("following::div[text()='Размер не должен превышать 100 КБ']");
    private final SelenideElement increaseVersionIcon = $x("//div[.='Выберите версию']/following::*[name()='svg'][2]");

    @Step("Сохранение объекта без изменения версии")
    public EntityPage saveWithoutPatchVersion(String alertText) {
        saveButton.click();
        Alert.green(alertText);
        return this;
    }

    @Step("Сохранение объекта со следующей патч-версией")
    public EntityPage saveWithPatchVersion(String alertText) {
        saveButton.click();
        new SaveDialog().saveWithNextPatchVersion(alertText);
        return this;
    }

    @Step("Сохранение объекта с версией '{newVersion}'")
    public EntityPage saveWithManualVersion(String newVersion, String alertText) {
        saveButton.click();
        new SaveDialog().saveWithVersion(newVersion, alertText);
        return this;
    }

    @Step("Проверка, что следующая предлагаемая версия для сохранения равна '{nextVersion}' и сохранение")
    public EntityPage checkNextVersionAndSave(String nextVersion, String alertText) {
        saveButton.click();
        new SaveDialog().checkNextVersionAndSave(nextVersion, alertText);
        return this;
    }

    @Step("Проверка недоступности сохранения объекта с некорректной версией '{newVersion}'")
    public EntityPage checkSaveWithInvalidVersion(String newVersion, String currentVersion) {
        saveButton.click();
        new SaveDialog().checkSaveWithInvalidVersion(newVersion, currentVersion);
        return this;
    }

    @Step("Проверка недоступности сохранения объекта с версией некорректного формата '{newVersion}'")
    public EntityPage checkSaveWithInvalidVersionFormat(String newVersion) {
        saveButton.click();
        new SaveDialog().checkSaveWithInvalidVersionFormat(newVersion);
        return this;
    }

    @Step("Проверка недоступности сохранения объекта при достижении лимита версий")
    public EntityPage checkVersionLimit() {
        $x("//div[text()='Достигнут предел допустимого значения версии. Нельзя сохранить следующую версию']")
                .shouldBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.disabled);
        return this;
    }

    @Step("Переход на вкладку '{title}'")
    public void goToTab(String title) {
        Tab.byText(title).switchTo();
    }

    @Step("Переход на вкладку 'История изменений'")
    public MeccanoAuditPage goToAuditTab() {
        goToTab("История изменений");
        Waiting.find(() -> !$x("//div[@role='progressbar']").isDisplayed(), Duration.ofSeconds(3));
        return new MeccanoAuditPage();
    }

    @Step("Проверка, что открыта вкладка '{title}'")
    public void checkTabIsSelected(String title) {
        Assertions.assertTrue(Tab.byText(title).isSelected(), "Вкладка " + title + " не выбрана");
    }

    @Step("Просмотр JSON и проверка отображения '{value}'")
    public EntityPage checkJSONcontains(String value) {
        viewJSONButton.click();
        $x("//div[@role='dialog']//span[text()='\"" + value + "\"']").shouldBe(Condition.visible);
        expandJSONView.click();
        expandJSONView.click();
        closeJSONView.click();
        return this;
    }

    @Step("Отмена в баннере о несохраненных изменениях и проверка текста")
    protected void dismissAlert(String text) {
        Assertions.assertTrue(switchTo().alert().getText().contains(text), "Текст баннера отличается от " + text);
        switchTo().alert().dismiss();
    }

    @Step("Продолжить в баннере о несохраненных изменениях и проверка текста")
    protected void acceptAlert(String text) {
        Assertions.assertTrue(switchTo().alert().getText().contains(text), "Текст баннера отличается от " + text);
        switchTo().alert().accept();
    }

    @Step("Добавление существующего тега")
    public void addExistingTag(String name) {
        tagsTab.switchTo();
        addTagButton.click();
        Select.byXpath("//input[@placeholder='Выберите тег или введите новый']/ancestor::div[select]").set(name);
        new Table("Наименование").getRow(0).get().$$x(".//button").get(1).click();
    }

    @Step("Добавление нового тега")
    public void addNewTag(String name) {
        tagsTab.switchTo();
        addTagButton.click();
        Select.byPlaceholder("Выберите тег или введите новый").getElement().setValue(name);
        SelenideElement rowElement = new Table("Наименование").getRow(0).get();
        rowElement.$x(".//input").sendKeys(Keys.ENTER);
        rowElement.$$x(".//button").get(1).click();
    }

    @Step("Удаление тега")
    public void deleteTag(String name) {
        tagsTab.switchTo();
        new Table("Наименование").getRowByColumnValue("Наименование", name).get().$x(".//button")
                .click();
        new Dialog("Удалить запись").clickButton("Удалить");
    }

    @Step("Поднятие версии и сохранение")
    public EntityPage increaseVersionAndSave(String nextVersion, String alertText) {
        increaseVersionIcon.click();
        new SaveDialog().checkNextVersionAndSave(nextVersion, alertText);
        return this;
    }
}
