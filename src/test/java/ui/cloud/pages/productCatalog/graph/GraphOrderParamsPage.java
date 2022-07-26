package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$x;

public class GraphOrderParamsPage extends GraphPage {

    private final SelenideElement schema = $x("//div[text()='JSON']/ancestor::div[4]//textarea");
    private final SelenideElement jsonSchemaButton = $x("//button[@id='JSON']");
    private final SelenideElement uiSchemaButton = $x("//button[@id='UI']");

    public GraphOrderParamsPage setJSONSchemaAndSave(String value) {
        jsonSchemaButton.click();
        schema.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        schema.setValue(value);
        saveGraphWithPatchVersion();
        return this;
    }

    public GraphOrderParamsPage setUISchemaAndSave(String value) {
        uiSchemaButton.click();
        schema.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        schema.setValue(value);
        saveGraphWithPatchVersion();
        return this;
    }

    public GraphOrderParamsPage checkJSONSchemaContains(String value) {
        jsonSchemaButton.click();
        $x("//div[contains(@class,'monaco-editor')]//span[contains(text(),'"
                + value + "')]").shouldBe(Condition.visible);
        return this;
    }
}
