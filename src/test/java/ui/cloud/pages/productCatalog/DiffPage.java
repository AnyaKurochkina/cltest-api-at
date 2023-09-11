package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import ui.elements.Select;

import static com.codeborne.selenide.Selenide.$x;

public class DiffPage extends EntityPage {

    private final Select versionToCompareWith = Select.byLabel("Версия для сравнения");

    @Step("Проверка, что выбрана текущая версия '{version}'")
    public DiffPage checkCurrentVersionInDiff(String version) {
        $x("//select[@disabled]/parent::div//div[text()='" + version + "']").shouldBe(Condition.visible);
        return this;
    }

    @Step("Выбор версии для сравнения '{version}' и проверка диффа")
    public DiffPage compareWithVersion(String version) {
        versionToCompareWith.set(version);
        $x("//span[text()='\"version\"']/following-sibling::span[text()='\"" + version + "\"']")
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Выбор версии для просмотра '{version}'")
    public DiffPage selectVersion(String version) {
        versionSelect.set(version);
        return this;
    }
}
