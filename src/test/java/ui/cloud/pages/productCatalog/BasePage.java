package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import ui.elements.Alert;
import ui.elements.Select;

import static com.codeborne.selenide.Selenide.$x;

public class BasePage {

    protected final Select versionDropDown = Select.byLabel("Выберите версию");
    protected final WebElement versionComparisonTab = $x("//button[span[text()='Сравнение версий']]");
    protected final SelenideElement selectedVersion = $x("//label[text()='Выберите версию']/..//div[@id='selectValueWrapper']/div");
    protected final SelenideElement saveButton = $x("//button[div[text()='Сохранить']]");
    protected final SelenideElement cancelButton = $x("//button[div[text()='Отмена']]");

    @Step("Сохранение объекта без изменения версии")
    public BasePage saveWithoutPatchVersion(String alertText) {
        saveButton.shouldBe(Condition.enabled).click();
        Alert.green(alertText);
        return this;
    }

    @Step("Сохранение объекта со следующей патч-версией")
    public BasePage saveWithPatchVersion(String alertText) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().saveWithNextPatchVersion(alertText);
        return this;
    }

    @Step("Сохранение объекта с версией '{newVersion}'")
    public BasePage saveWithManualVersion(String newVersion, String alertText) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().saveWithVersion(newVersion, alertText);
        return this;
    }

    @Step("Проверка, что следующая предлагаемая версия для сохранения равна '{nextVersion}' и сохранение")
    public BasePage checkNextVersionAndSave(String nextVersion, String alertText) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().checkNextVersionAndSave(nextVersion, alertText);
        return this;
    }

    @Step("Проверка недоступности сохранения объекта с некорректной версией '{newVersion}'")
    public BasePage checkSaveWithInvalidVersion(String newVersion, String currentVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().checkSaveWithInvalidVersion(newVersion, currentVersion);
        return this;
    }

    @Step("Проверка недоступности сохранения объекта с версией некорректного формата '{newVersion}'")
    public BasePage checkSaveWithInvalidVersionFormat(String newVersion) {
        saveButton.shouldBe(Condition.enabled).click();
        new SaveDialog().checkSaveWithInvalidVersionFormat(newVersion);
        return this;
    }

    @Step("Проверка недоступности сохранения объекта при достижении лимита версий")
    public BasePage checkVersionLimit() {
        $x("//div[text()='Достигнут предел допустимого значения версии. Нельзя сохранить следующую версию']")
                .shouldBe(Condition.visible);
        saveButton.shouldBe(Condition.disabled);
        return this;
    }

    @Step("Переход на вкладку '{title}'")
    public void goToTab(String title) {
        SelenideElement tab = $x("//button[span[text()='" + title + "']]");
        if (tab.getAttribute("aria-selected").equals("false")) {
            tab.scrollIntoView(false).click();
        }
    }

}
