package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;
import ui.models.GraphModifier;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class GraphModifiersPage extends GraphPage {

    private final SelenideElement addModifierButton = $x("//div[text()='Добавить']//ancestor::button");
    private final Input nameInput = Input.byXpath("//form//input[@name='name']");
    private final Input numberInput = Input.byLabelV2("Порядок применения");
    private final Select schemaSelect = Select.byLabel("Схема");
    private final Select typeSelect = Select.byLabel("Способ изменения");
    private final MultiSelect envTypeSelect = MultiSelect.byLabel("Типы сред");
    private final Input pathInput = Input.byXpath("//form//input[@name='path']");
    private final TextArea modifierData = TextArea.byXPath("//form//span[text()='ModifierData']/ancestor::div[2]//textarea");
    private final SelenideElement formSaveButton = $x("//form//div[text()='Сохранить']/parent::button");
    private final SelenideElement formCancelButton = $x("//form//div[text()='Отмена']/parent::button");
    private final Button jsonSchemaButton = Button.byId("JSON");
    private final Button uiSchemaButton = Button.byId("UI");
    private final Button staticDataButton = Button.byId("StaticData");
    private final SelenideElement modifierNameValidationHint =
            $x("//form//div[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement modifierNumberValidationHint =
            $x("//form//div[text()='Допустимое значение от 1 до 2147483647']");
    private final SelenideElement nonUniqueModifierNameHint =
            $x("//form//div[text()='Такое наименование уже существует']");
    private final SelenideElement nonUniqueModifierNumberHint =
            $x("//form//div[text()='Порядковый номер уже существует']");

    @Step("Добавление модификатора '{modifier.name}' и сохранение графа")
    public GraphModifiersPage addModifierAndSave(GraphModifier modifier) {
        addModifierButton.click();
        Waiting.sleep(500);
        nameInput.setValue(modifier.getName());
        envTypeSelect.set(modifier.getEnvs());
        schemaSelect.set(modifier.getSchema());
        typeSelect.set(modifier.getType());
        pathInput.setValue(modifier.getPath());
        modifierData.setValue(modifier.getModifierData());
        formSaveButton.click();
        Alert.green(SAVE_GRAPH_ALERT_TEXT);
        return this;
    }

    @Step("Проверка атрибутов модификатора")
    public GraphModifiersPage checkModifierAttributes(GraphModifier modifier) {
        $x("//td[text()='{}']/parent::tr//button[1]", modifier.getName()).click();
        nameInput.getInput().shouldHave(Condition.exactValue(modifier.getName()));
        Assertions.assertEquals(modifier.getEnvs()[0], envTypeSelect.getValue());
        numberInput.getInput().shouldHave(Condition.exactValue(modifier.getNumber()));
        $x("//form//div[text()='" + modifier.getSchema() + "']").shouldBe(Condition.visible);
        pathInput.getInput().shouldHave(Condition.exactValue(modifier.getPath()));
        $x("//form//div[contains(@class,'monaco-editor')]//span[contains(text(),'"
                + modifier.getModifierDataSubstring() + "')]")
                .shouldBe(Condition.visible);
        formCancelButton.click();
        return this;
    }

    @Step("Редактирование модификатора '{modifier.name}' и сохранение графа")
    public GraphModifiersPage editModifierAndSave(GraphModifier modifier) {
        $x("//td[text()='{}']/parent::tr//button[1]", modifier.getName()).scrollIntoView(false).click();
        nameInput.setValue(modifier.getName());
        envTypeSelect.set(modifier.getEnvs());
        schemaSelect.set(modifier.getSchema());
        typeSelect.set(modifier.getType());
        pathInput.setValue(modifier.getPath());
        modifierData.setValue(modifier.getModifierData());
        formSaveButton.click();
        Alert.green(SAVE_GRAPH_ALERT_TEXT);
        return this;
    }

    @Step("Проверка валидации недопустимого названия модификатора")
    public GraphModifiersPage checkModifierNameValidation(String[] names) {
        addModifierButton.click();
        TestUtils.wait(500);
        for (String name : names) {
            nameInput.setValue(name);
            TestUtils.wait(600);
            if (!modifierNameValidationHint.exists()) {
                nameInput.getInput().sendKeys("t");
            }
            modifierNameValidationHint.shouldBe(Condition.visible);
        }
        formSaveButton.shouldBe(Condition.disabled);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального названия '{name}' модификатора")
    public GraphModifiersPage checkNonUniqueModifierName(String name) {
        addModifierButton.click();
        nameInput.setValue(name);
        nonUniqueModifierNameHint.shouldBe(Condition.visible);
        formSaveButton.shouldBe(Condition.disabled);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального номера '{number}' модификатора")
    public GraphModifiersPage checkNonUniqueModifierNumber(String number) {
        addModifierButton.click();
        numberInput.setValue(number);
        nonUniqueModifierNumberHint.shouldBe(Condition.visible);
        formSaveButton.shouldBe(Condition.disabled);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка валидации недопустимого номера модификатора")
    public GraphModifiersPage checkModifierNumberValidation() {
        addModifierButton.click();
        for (String number : new String[]{"0", "-1"}) {
            numberInput.setValue(number);
            TestUtils.wait(600);
            modifierNumberValidationHint.shouldBe(Condition.visible);
        }
        formSaveButton.shouldBe(Condition.disabled);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка, что JSONSchema содержит значение '{value}'")
    public GraphModifiersPage checkModifiedJSONSchemaContains(String value) {
        Waiting.sleep(500);
        jsonSchemaButton.getButton().scrollIntoView(scrollCenter).click();
        $x("//div[contains(@class,'monaco-editor')]//span[contains(text(),'" + value + "')]")
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка, что UISchema содержит значение '{value}'")
    public GraphModifiersPage checkModifiedUISchemaContains(String value) {
        Waiting.sleep(500);
        uiSchemaButton.getButton().scrollIntoView(true).click();
        $x("//div[contains(@class,'monaco-editor')]//span[contains(text(),'" + value + "')]")
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка, что StaticData содержит значение '{value}'")
    public GraphModifiersPage checkModifiedStaticDataContains(String value) {
        Waiting.sleep(500);
        staticDataButton.getButton().scrollIntoView(scrollCenter).click();
        $x("//div[contains(@class,'monaco-editor')]//span[contains(text(),'" + value + "')]")
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Удаление модификатора '{name}'")
    public GraphModifiersPage deleteModifier(String name) {
        $x("//td[text()='{}']/parent::tr//button[2]", name).scrollIntoView(scrollCenter).click();
        new DeleteDialog().submitAndDelete("Модификатор успешно удален");
        Waiting.sleep(1100);
        Assertions.assertFalse($x("//td[@value='" + name + "']").exists());
        return this;
    }

    @Step("Выбор типа среды просмотра модификатора")
    public GraphModifiersPage setEnvType(String value) {
        $x("//span[text()='Тип среды']/following::div[text()='{}']", value)
                .scrollIntoView(scrollCenter).click();
        return this;
    }

    @Step("Выбор среды просмотра модификатора")
    public GraphModifiersPage setEnv(String value) {
        $x("//span[text()='Среда']/following::div[text()='{}']", value)
                .scrollIntoView(scrollCenter).click();
        return this;
    }
}
