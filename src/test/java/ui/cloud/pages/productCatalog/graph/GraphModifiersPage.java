package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;
import ui.cloud.tests.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.uiModels.GraphModifier;

import static com.codeborne.selenide.Selenide.$x;

public class GraphModifiersPage extends GraphPage {

    private final SelenideElement addModifierButton = $x("//div[text()='Добавить']//ancestor::button");
    private final SelenideElement nameInput = $x("//form//input[@name='name']");
    private final SelenideElement devEnvCheckbox = $x("//form//input[@name='dev']");
    private final SelenideElement testEnvCheckbox = $x("//form//input[@name='test']");
    private final SelenideElement prodEnvCheckbox = $x("//form//input[@name='prod']");
    private final SelenideElement numberInput = $x("//form//label[contains(text(),'Порядок применения')]/..//input");
    private final SelenideElement showSchemas = $x("//form//label[text()='Схема']/..//*[name()='svg']");
    private final SelenideElement showTypes = $x("//form//label[text()='Способ изменения']/..//*[name()='svg']");
    private final SelenideElement pathInput = $x("//form//input[@name='path']");
    private final SelenideElement modifierData = $x("//form//span[text()='ModifierData']/ancestor::div[2]//textarea");
    private final SelenideElement formSaveButton = $x("//form//span[text()='Сохранить']/parent::button");
    private final SelenideElement formCancelButton = $x("//form//span[text()='Отмена']/parent::button");
    private final SelenideElement jsonSchemaButton = $x("//button[@id='JSON']");
    private final SelenideElement uiSchemaButton = $x("//button[@id='UI']");
    private final SelenideElement staticDataButton = $x("//button[@id='StaticData']");
    private final SelenideElement modifierNameValidationHint = $x("//form//div[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement modifierNumberValidationHint = $x("//form//div[text()='Допустимое значение от 1 до 2147483647']");
    private final SelenideElement devEnvRadioButton = $x("//input[@type='radio' and @name='dev']/following-sibling::span");
    private final SelenideElement testEnvRadioButton = $x("//input[@type='radio' and @name='test']/following-sibling::span");
    private final SelenideElement prodEnvRadioButton = $x("//input[@type='radio' and @name='prod']/following-sibling::span");
    private final SelenideElement nonUniqueModifierNameHint = $x("//form//div[text()='Такое наименование уже существует']");
    private final SelenideElement nonUniqueModifierNumberHint = $x("//form//div[text()='Порядковый номер уже существует']");

    @Step("Добавление модификатора '{modifier.name}' и сохранение графа")
    public GraphModifiersPage addModifierAndSave(GraphModifier modifier) {
        addModifierButton.click();
        TestUtils.wait(500);
        nameInput.setValue(modifier.getName());
        setEnvs(modifier.getEnvs());
        showSchemas.click();
        $x("//li[@value='" + modifier.getSchema() + "']").shouldBe(Condition.enabled).click();
        showTypes.click();
        $x("//li[@value='" + modifier.getType() + "']").shouldBe(Condition.enabled).click();
        pathInput.setValue(modifier.getPath());
        modifierData.setValue(modifier.getModifierData());
        formSaveButton.click();
        new Alert().checkText("Граф успешно сохранен").checkColor(Alert.Color.GREEN).close();
        return this;
    }

    @Step("Проверка атрибутов модификатора")
    public GraphModifiersPage checkModifierAttributes(GraphModifier modifier) {
        $x("//td[@value='" + modifier.getName() + "']/parent::tr//button[1]").click();
        nameInput.shouldHave(Condition.exactValue(modifier.getName()));
        checkSelectedEnvs(modifier.getEnvs());
        numberInput.shouldHave(Condition.exactValue(modifier.getNumber()));
        $x("//form//div[text()='" + modifier.getSchema() + "']").shouldBe(Condition.visible);
        pathInput.shouldHave(Condition.exactValue(modifier.getPath()));
        $x("//form//div[contains(@class,'monaco-editor')]//span[contains(text(),'"
                + modifier.getModifierDataSubstring() + "')]")
                .shouldBe(Condition.visible);
        formCancelButton.click();
        return this;
    }

    @Step("Редактирование модификатора '{modifier.name}' и сохранение графа")
    public GraphModifiersPage editModifierAndSave(GraphModifier modifier) {
        $x("//td[@value='" + modifier.getName() + "']/parent::tr//button[1]").click();
        nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        nameInput.setValue(modifier.getName());
        clearSelectedEnvs();
        setEnvs(modifier.getEnvs());
        showSchemas.click();
        $x("//li[@value='" + modifier.getSchema() + "']").shouldBe(Condition.enabled).click();
        showTypes.click();
        $x("//li[@value='" + modifier.getType() + "']").shouldBe(Condition.enabled).click();
        pathInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        pathInput.setValue(modifier.getPath());
        modifierData.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        modifierData.setValue(modifier.getModifierData());
        formSaveButton.click();
        return this;
    }

    @Step("Проверка валидации недопустимого названия модификатора")
    public GraphModifiersPage checkModifierNameValidation(String[] names) {
        addModifierButton.click();
        TestUtils.wait(500);
        for (String name : names) {
            nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
            nameInput.setValue(name);
            TestUtils.wait(600);
            if (!modifierNameValidationHint.exists()) {
                nameInput.sendKeys("t");
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
        nameInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        nameInput.setValue(name);
        nonUniqueModifierNameHint.shouldBe(Condition.visible);
        formSaveButton.shouldBe(Condition.disabled);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального номера '{number}' модификатора")
    public GraphModifiersPage checkNonUniqueModifierNumber(String number) {
        addModifierButton.click();
        numberInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
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
            numberInput.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
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
        TestUtils.wait(500);
        TestUtils.scroll(300);
        jsonSchemaButton.click();
        $x("//div[contains(@class,'monaco-editor')]//span[contains(text(),'" + value + "')]")
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка, что UISchema содержит значение '{value}'")
    public GraphModifiersPage checkModifiedUISchemaContains(String value) {
        TestUtils.wait(500);
        TestUtils.scroll(300);
        uiSchemaButton.click();
        $x("//div[contains(@class,'monaco-editor')]//span[contains(text(),'" + value + "')]")
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка, что StaticData содержит значение '{value}'")
    public GraphModifiersPage checkModifiedStaticDataContains(String value) {
        TestUtils.wait(500);
        TestUtils.scroll(300);
        staticDataButton.click();
        $x("//div[contains(@class,'monaco-editor')]//span[contains(text(),'" + value + "')]")
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Удаление модификатора '{name}'")
    public GraphModifiersPage deleteModifier(String name) {
        $x("//td[@value='" + name + "']/parent::tr//button[2]").click();
        new DeleteDialog().inputValidId("Модификатор успешно удален");
        TestUtils.wait(1100);
        Assertions.assertFalse($x("//td[@value='" + name + "']").exists());
        return this;
    }

    @Step("Задание сред для модификатора")
    private void setEnvs(String[] envs) {
        for (String env : envs) {
            switch (env) {
                case "dev":
                    devEnvCheckbox.click();
                    break;
                case "test":
                    testEnvCheckbox.click();
                    break;
                case "prod":
                    prodEnvCheckbox.click();
                    break;
            }
        }
    }

    @Step("Проверка выбранных сред модификатора")
    private void checkSelectedEnvs(String[] envs) {
        for (String env : envs) {
            $x("//form//input[@name='" + env + "' and @checked]").shouldBe(Condition.visible);
        }
    }

    @Step("Очистка выбранных сред модификатора")
    private void clearSelectedEnvs() {
        for (String env : new String[]{"dev", "test", "prod"}) {
            if ($x("//form//input[@name='" + env + "' and @checked]").exists()) {
                $x("//form//input[@name='" + env + "' and @checked]").click();
            }
        }
    }

    @Step("Выбор среды просмотра модификатора")
    public GraphModifiersPage selectEnv(String env) {
        switch (env) {
            case "dev":
                devEnvRadioButton.click();
                break;
            case "test":
                testEnvRadioButton.click();
                break;
            case "prod":
                prodEnvRadioButton.click();
                break;
        }
        return this;
    }
}
