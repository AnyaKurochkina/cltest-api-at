package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$x;

public class GraphOrderParamsPage extends GraphPage {

    private final SelenideElement schema = $x("//button[@id='JSON']/following::textarea[1]");
    private final SelenideElement jsonSchemaButton = $x("//button[@id='JSON']");
    private final SelenideElement uiSchemaButton = $x("//button[@id='UI']");

    @Step("Задание для JSONSchema значения '{value}'")
    public GraphOrderParamsPage setJSONSchemaAndSave(String value) {
        jsonSchemaButton.click();
        schema.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        schema.setValue(value);
        saveGraphWithPatchVersion();
        return this;
    }

    @Step("Задание для UISchema значения '{value}'")
    public GraphOrderParamsPage setUISchemaAndSave(String value) {
        uiSchemaButton.click();
        schema.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        schema.setValue(value);
        saveGraphWithPatchVersion();
        return this;
    }

    @Step("Проверка, что JSONSchema содержит значение '{value}'")
    public GraphOrderParamsPage checkJSONSchemaContains(String value) {
        jsonSchemaButton.click();
        $x("//div[contains(@class,'monaco-editor')]//span[contains(text(),'"
                + value + "')]").shouldBe(Condition.visible);
        return this;
    }
}
