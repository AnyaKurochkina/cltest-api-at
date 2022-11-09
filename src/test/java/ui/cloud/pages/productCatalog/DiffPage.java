package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import ui.elements.DropDown;

import static com.codeborne.selenide.Selenide.$x;

public class DiffPage extends BasePage {

    private final DropDown versionToCompareWith = DropDown.byLabel("Версия для сравнения");

    @Step("Проверка, что выбрана текущая версия '{version}'")
    public DiffPage checkCurrentVersionInDiff(String version) {
        $x("//select[@disabled]/parent::div//div[text()='" + version + "']").shouldBe(Condition.visible);
        return this;
    }

    @Step("Выбор версии для сравнения '{version}' и проверка диффа")
    public DiffPage compareWithVersion(String version) {
        versionToCompareWith.selectByTitle(version);
        $x("//span[text()='\"version\"']/following-sibling::span[text()='\"" + version + "\"']")
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Выбор версии для просмотра '{version}'")
    public DiffPage selectVersion(String version) {
        versionDropDown.selectByTitle(version);
        return this;
    }
}
