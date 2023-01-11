package ui.cloud.pages.productCatalog.graph;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Input;
import ui.elements.Select;
import ui.elements.TextArea;
import ui.elements.TypifiedElement;
import ui.models.Node;
import ui.models.SubgraphNode;
import ui.models.TemplateNode;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.actions;
import static core.helper.StringUtils.$x;

public class GraphNodesPage extends GraphPage {

    private final SelenideElement addNodeButton = $x("//button[@aria-label = 'add node']");
    private final SelenideElement editNodeButton = $x("//button[@aria-label = 'edit node']");
    private final SelenideElement copyNode = $x("//li[@id='clone_node']");
    private final SelenideElement deleteNodesButton = $x("//button[@aria-label = 'delete items']");
    private final SelenideElement nodeName = $x("//form//input[@name = 'name']");
    private final SelenideElement nodeDescription = $x("//form//input[@name = 'description']");
    private final Input subgraphInput = Input.byLabelV2("Подграф");
    private final Input templateInput = Input.byLabelV2("Шаблон");
    private final SelenideElement formAddNodeButton = $x("//form//div[text() = 'Добавить']//parent::button");
    private final SelenideElement formSaveNodeButton = $x("//form//div[text() = 'Сохранить']//parent::button");
    private final SelenideElement formCancelButton = $x("//form//div[text() = 'Отмена']//ancestor::button");
    private final TextArea inputTextArea = TextArea.byLabel("Input");
    private final TextArea outputTextArea = TextArea.byLabel("Output");
    private final SelenideElement numberInput = $x("//input[@name='number']");
    private final SelenideElement timeoutInput = $x("//input[@name='timeout']");
    private final SelenideElement countInput = $x("//input[@name='count']");
    private final SelenideElement forEachInput = $x("//input[@name='for_each']");
    private final SelenideElement conditionInput = $x("//input[@name='condition']");
    private final SelenideElement onPrebillingToggle = $x("//input[@name='on_prebilling']");
    private final SelenideElement runOnRollbackToggle = $x("//input[@name='run_on_rollback']");
    private final SelenideElement holdToggle = $x("//input[@name='hold']");
    private final SelenideElement isSequentialToggle = $x("//input[@name='is_sequential']");
    private final SelenideElement damageOrderOnErrorToggle = $x("//form//input[@name='damage_order_on_error']");
    private final SelenideElement loggingLevelSelect = $x("//div[text()='Уровень логирования']/following::select[1]");
    private final SelenideElement nameRequiredFieldHint = $x("//label[contains(text(),'Название')]/ancestor::div[2]//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement descriptionRequiredFieldHint = $x("//label[contains(text(),'Описание')]/ancestor::div[2]//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement incorrectNumberHint = $x("//label[text()='Номер']/ancestor::div[2]//div[text()='Введите корректное значение']");
    private final SelenideElement incorrectTimeoutHint = $x("//label[text()='Время ожидания, сек']/ancestor::div[2]//div[text()='Введите корректное значение']");
    private final SelenideElement nameNonUniqueHint = $x("//div[text()='Узел с данным названием уже существует']");
    private final SelenideElement onPrebillingToggleOn = $x("//p[text()='Запуск узла на предбиллинге']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement runOnRollbackToggleOn = $x("//p[text()='Запуск узла при откате']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement holdToggleOn = $x("//p[text()='Постановка узла на паузу']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement isSequentialToggleOn = $x("//p[text()='Последовательное выполнение итераций']/parent::div//span[contains(@class,'checked')]");
    private final SelenideElement damageOrderOnErrorToggleOn = $x("//p[text()='Переводить заказ в статус \"Ошибка\"']/ancestor::div[2]//span[contains(@class,'checked')]");
    private final SelenideElement showTemplateVersions = $x("(//label[text()='Версия'])[1]/following::*[name()='svg'][1]");
    private final SelenideElement showSubgraphVersions = $x("(//label[text()='Версия'])[2]/following::*[name()='svg'][1]");
    private final SelenideElement templateVersion = $x("(//label[text()='Версия'])[1]/following::div[@id='selectValueWrapper']");
    private final SelenideElement subgraphVersion = $x("(//label[text()='Версия'])[2]/following::div[@id='selectValueWrapper']");
    private final SelenideElement additionalTab = $x("//button[text()='Дополнительное']");
    private final SelenideElement paramsTab = $x("//button[text()='Параметры']");
    private final SelenideElement staticData = $x("//label[text()='Static data']/parent::div//textarea");
    private final SelenideElement searchNodesInput = $x("//input[@placeholder='Поиск...']");

    @Step("Добавление узла графа '{node.name}' и сохранение графа")
    public GraphNodesPage addNodeAndSave(Node node) {
        addNodeButton.click();
        TestUtils.wait(1000);
        nodeName.setValue(node.getName());
        nodeDescription.setValue(node.getDescription());
        if (node instanceof SubgraphNode) {
            subgraphInput.click();
            subgraphInput.setValue(((SubgraphNode) node).getSubgraphName());
            $x("//div[contains(text(),'" + ((SubgraphNode) node).getSubgraphName() + "')]")
                    .shouldBe(Condition.enabled).click();
            paramsTab.click();
            inputTextArea.setValue(node.getInput());
            outputTextArea.setValue(node.getOutput());
            additionalTab.click();
            numberInput.setValue(String.valueOf(node.getNumber()));
            timeoutInput.setValue(String.valueOf(node.getTimeout()));
            loggingLevelSelect.shouldBe(Condition.disabled);
            countInput.setValue(String.valueOf(node.getCount()));
        }
        if (node instanceof TemplateNode) {
            templateInput.click();
            templateInput.setValue(((TemplateNode) node).getTemplateName());
            $x("//div[contains(text(),'" + ((TemplateNode) node).getTemplateName() + "')]")
                    .shouldBe(Condition.enabled).click();
        }
        additionalTab.click();
        onPrebillingToggle.click();
        runOnRollbackToggle.click();
        holdToggle.click();
        isSequentialToggle.click();
        damageOrderOnErrorToggle.click();
        formAddNodeButton.click();
        saveGraphWithPatchVersion();
        TestUtils.wait(1000);
        return this;
    }

    @Step("Редактирование узла '{node.name}' с подграфом")
    public GraphNodesPage editSubgraphNode(SubgraphNode node, String version, String description) {
        TestUtils.scrollToTheBottom();
        selectNodeInGraph(node);
        TestUtils.scrollToTheTop();
        editNodeButton.click();
        nodeDescription.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        nodeDescription.setValue(description);
        showSubgraphVersions.click();
        $x("//div[@title='" + version + "']").shouldBe(Condition.enabled).click();
        formSaveNodeButton.click();
        saveGraphWithPatchVersion();
        node.setSubgraphVersion(version);
        node.setDescription(description);
        TestUtils.wait(1000);
        return this;
    }

    @Step("Редактирование узла '{node.name}' с шаблоном")
    public GraphNodesPage editTemplateNode(TemplateNode node, String version, String description) {
        TestUtils.scrollToTheBottom();
        selectNodeInGraph(node);
        TestUtils.scrollToTheTop();
        editNodeButton.click();
        nodeDescription.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        nodeDescription.setValue(description);
        showTemplateVersions.click();
        $x("//div[@title='" + version + "']").shouldBe(Condition.enabled).click();
        formSaveNodeButton.click();
        saveGraphWithPatchVersion();
        node.setTemplateVersion(version);
        node.setDescription(description);
        TestUtils.wait(1000);
        return this;
    }

    @Step("Копирование узла графа '{node.name}' и сохранение графа")
    public GraphNodesPage copyNodeAndSave(Node node) {
        String cloneName = node.getName() + "_clone";
        TestUtils.scrollToTheBottom();
        selectNodeInGraph(node);
        TestUtils.scrollToTheTop();
        actions().pause(1000)
                .moveToElement($x("//div[@class='g6-grid-container']/following-sibling::canvas"))
                .moveByOffset(0, 70)
                .contextClick()
                .perform();
        copyNode.scrollIntoView(TypifiedElement.scrollCenter).click();
        nodeName.shouldHave(Condition.exactValue(cloneName));
        TestUtils.wait(500);
        formAddNodeButton.click();
        saveGraphWithPatchVersion();
        return this;
    }

    @Step("Проверка отсутствия узла '{node.name}' в списке узлов")
    public GraphNodesPage checkNodeNotFound(SubgraphNode node) {
        $x("//div[text()='" + node.getDescription() + "']/..//*[name()='svg' and @class]").shouldBe(Condition.not(Condition.visible));
        return this;
    }

    @Step("Проверка некорректного заполнения полей при добавлении узла")
    public GraphNodesPage checkAddNodeSubgraphDisabled(SubgraphNode node) {
        addNodeButton.click();
        nodeName.setValue(node.getName());
        nodeDescription.setValue(node.getDescription());
        subgraphInput.click();
        subgraphInput.setValue(node.getSubgraphName());
        if (node.getName().isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (node.getDescription().isEmpty()) {
            descriptionRequiredFieldHint.shouldBe(Condition.visible);
        }
        additionalTab.click();
        numberInput.setValue(String.valueOf(node.getNumber()));
        timeoutInput.setValue(String.valueOf(node.getTimeout()));
        loggingLevelSelect.shouldBe(Condition.disabled);
        countInput.setValue(String.valueOf(node.getCount()));
        if (node.getNumber().equals("0")) {
            incorrectNumberHint.shouldBe(Condition.visible);
        }
        if (node.getTimeout().equals("0")) {
            incorrectTimeoutHint.shouldBe(Condition.visible);
        }
        formAddNodeButton.shouldBe(Condition.disabled);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального имени узла при добавлении узла")
    public GraphNodesPage checkAddNodeSubgraphWithNonUniqueNameDisabled(SubgraphNode node) {
        TestUtils.scrollToTheTop();
        addNodeButton.click();
        nodeName.setValue(node.getName());
        nameNonUniqueHint.shouldBe(Condition.visible);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка значений атрибутов узла '{node.name}'")
    public GraphNodesPage checkNodeAttributes(Node node) {
        if (node.getNumber().equals("")) {
            node.setNumber("1");
        }
        TestUtils.scrollToTheBottom();
        selectNodeInGraph(node);
        TestUtils.scrollToTheTop();
        editNodeButton.click();
        TestUtils.wait(500);
        nodeName.shouldHave(Condition.exactValue(node.getName()));
        nodeDescription.shouldHave(Condition.exactValue(node.getDescription()));
        if (node instanceof SubgraphNode) {
            Select subgraphDropDown = Select.byLabel("Подграф");
            Assertions.assertTrue(subgraphDropDown.getElement().getText().contains(((SubgraphNode) node).getSubgraphName()));
            subgraphVersion.shouldHave(Condition.exactText(((SubgraphNode) node).getSubgraphVersion()));
        }
        if (node instanceof TemplateNode) {
            $x("//div[text() = '" + ((TemplateNode) node).getTemplateName() + "']").shouldBe(Condition.visible);
            templateVersion.shouldHave(Condition.exactText(((TemplateNode) node).getTemplateVersion()));
        }
        paramsTab.click();
        $x("//form//label[text()='Input']/following::div[contains(@class,'view-lines')][1]//span[contains(text(),'{}')]",
                node.getInputKey()).shouldBe(Condition.visible);
        $x("//form//label[text()='Output']/following::div[contains(@class,'view-lines')][1]//span[contains(text(),'{}')]",
                node.getOutputKey()).shouldBe(Condition.visible);
        additionalTab.click();
        numberInput.shouldHave(Condition.exactValue(node.getNumber()));
        //timeoutInput.shouldHave(Condition.exactValue(node.getTimeout()));
        countInput.shouldHave(Condition.exactValue(node.getCount()));
        onPrebillingToggleOn.shouldBe(Condition.visible);
        runOnRollbackToggleOn.shouldBe(Condition.visible);
        holdToggleOn.shouldBe(Condition.visible);
        isSequentialToggleOn.shouldBe(Condition.visible);
        damageOrderOnErrorToggleOn.shouldBe(Condition.visible);
        formCancelButton.click();
        return this;
    }

    @Step("Проверка, что узел '{node.name}' найден при поиске '{text}'")
    public GraphNodesPage findNode(String text, Node node) {
        if (node.getNumber().equals("")) {
            node.setNumber("1");
        }
        searchNodesInput.setValue(text);
        TestUtils.wait(500);
        $x("//div[text()='{}. {} ({})']/..//*[name()='svg' and @class]", node.getNumber(), node.getDescription(), node.getName())
                .shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка, что узел '{node.name}' не найден при поиске '{text}'")
    public GraphNodesPage checkNodeNotFound(String text, Node node) {
        searchNodesInput.setValue(text);
        Assertions.assertFalse($x("//div[text()='" + node.getNumber() + ". " + node.getDescription() + "']")
                .exists());
        return this;
    }

    @Step("Удаление узла '{node.name}' и сохранение графа")
    public GraphNodesPage deleteNodeAndSave(Node node) {
        TestUtils.scrollToTheBottom();
        selectNodeInGraph(node);
        TestUtils.scrollToTheTop();
        deleteNodesButton.click();
        saveGraphWithPatchVersion();
        return this;
    }

    @Step("Задать для StaticData значение '{value}'")
    public GraphNodesPage setStaticData(String value) {
        staticData.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
        staticData.setValue(value);
        saveGraphWithPatchVersion();
        return this;
    }

    private void selectNodeInGraph(Node node) {
        if (node.getNumber().equals("")) {
            node.setNumber("1");
        }
        $x("//div[text()='{}. {} ({})']/..//*[name()='svg' and @class]", node.getNumber(), node.getDescription(), node.getName())
                .click();

    }
}
