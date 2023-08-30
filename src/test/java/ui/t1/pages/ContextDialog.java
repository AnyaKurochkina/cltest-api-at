package ui.t1.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Button;
import ui.elements.Dialog;

import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ContextDialog extends Dialog {
    Button all = Button.byId("All");
    SelenideElement organization = $x("//*[name()='path' and @d = 'M12.714 3.23a1.35 1.35 0 00-1.448.001L4.563 7.495c-1.14.725-.626 2.489.725 2.489h13.42c1.351 0 1.864-1.765.723-2.49L12.714 3.23zm-.75 1.098a.05.05 0 01.054 0l6.716 4.264a.077.077 0 01.02.016l.002.003.001.002a.061.061 0 01-.001.035.062.062 0 01-.018.03l-.003.002h-.002a.078.078 0 01-.025.004H5.288a.078.078 0 01-.026-.003l-.005-.003a.062.062 0 01-.017-.03.062.062 0 01-.002-.035l.003-.005a.078.078 0 01.02-.016l6.703-4.264z']/ancestor::div[@class='title-wrapper']");

    public ContextDialog() {
        super("Выберите контекст");
    }

    public IndexPage selectOrganization(String orgName) {
        $x("//*[@id='selectValueWrapper']").click();
        $x("//div[contains(text(), '{}')]", orgName).shouldBe(Condition.visible).click();
        all.click();
        organization.shouldBe(Condition.visible).click();
        assertTrue($x("//div[contains(text(), '{}')]", orgName).isDisplayed());
        return new IndexPage();
    }

}
