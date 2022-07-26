package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class VersionComparisonPage extends GraphPage {

    private final SelenideElement showVersions = $x("(//p[text()='Версия']/parent::div//*[name()='svg'])[2]");

    public VersionComparisonPage checkCurrentVersion(String version) {
        $x("//select[@disabled]/parent::div//div[text()='" + version + "']").shouldBe(Condition.visible);
        return this;
    }

    public VersionComparisonPage compareToVersion(String version) {
        showVersions.click();
        $x("//div[@title='" + version + "']").shouldBe(Condition.enabled).click();
        $x("//span[text()='\"compare_with_version\"']/following-sibling::span[contains(text(),'" + version + "')]")
                .shouldBe(Condition.visible);
        $x("//span[text()='\"diff\"']").shouldBe(Condition.visible);
        $x("//span[text()='\"changed_by_user\"']").shouldBe(Condition.visible);
        return this;
    }
}
