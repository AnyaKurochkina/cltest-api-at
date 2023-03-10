package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import lombok.Getter;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Assertions;
import ui.elements.TypifiedElement;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;

class SwitchV2 implements TypifiedElement {
    @Getter
    private final SelenideElement element;

    public SwitchV2(SelenideElement element) {
        this.element = element;
    }

    public static SwitchV2 byInputName(String name) {
        return new SwitchV2($x("//input[@name='{}']/ancestor::span[contains(@class, 'switchBase')]", name));
    }

    public static SwitchV2 byXPath(@Language("XPath") String xPath) {
        return new SwitchV2($x(xPath));
    }

    public boolean isEnabled() {
        return element.is(Condition.attributeMatching("class", "^.*checked"));
    }

    public void setEnabled(boolean enabled) {
        if (isEnabled() != enabled)
            element.$x(".//input")
                    .hover()
                    .shouldBe(clickableCnd)
                    .click();
        Waiting.sleep(200);
        Assertions.assertEquals(enabled, isEnabled());
    }
}
