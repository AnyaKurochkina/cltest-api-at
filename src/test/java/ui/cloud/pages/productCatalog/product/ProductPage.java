package ui.cloud.pages.productCatalog.product;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.product.Categories;
import models.cloud.productCatalog.product.Product;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.GraphSteps;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.*;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.back;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ui.elements.TypifiedElement.scrollCenter;

@Getter
public class ProductPage extends EntityPage {

    private final SelenideElement productsListLink = $x("//a[text()='Список продуктов']");
    private final Input authorInput = Input.byName("author");
    private final Input descriptionInput = Input.byName("description");
    private final Input maxCountInput = Input.byName("max_count");
    private final Input numberInput = Input.byName("number");
    private final String saveProductAlertText = "Продукт успешно изменен";
    private final TextArea info = TextArea.byLabel("Информация");
    private final TextArea extraData = TextArea.byLabel("Extra data");
    private final Select categorySelect = Select.byLabel("Категория");
    private final Select categoryV2Select = Select.byLabel("КатегорияV2");
    private final Select onRequestSelect = Select.byLabel("Продукт по запросу");
    private final Select paymentSelect = Select.byLabel("Выбор оплаты");
    private final Switch inGeneralListSwitch = Switch.byText("В общем списке маркетплейса");
    private final Switch isOpenSwitch = Switch.byText("Открытый");
    private final SelenideElement nameValidationHint =
            $x("//div[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement nonUniqueNameValidationHint =
            $x("//input[@name='name']/following::div[text()='Продукт с таким именем уже существует']");
    private final String requiredFieldText = "Поле обязательно для заполнения";
    private final SelenideElement nameRequiredFieldHint =
            nameInput.getInput().$x("ancestor::div[2]//div[text()='" + requiredFieldText + "']");
    private final SelenideElement titleRequiredFieldHint =
            titleInput.getInput().$x("ancestor::div[2]//div[text()='" + requiredFieldText + "']");
    private final SelenideElement graphRequiredFieldHint =
            graphSelect.getElement().$x(".//div[text()='" + requiredFieldText + "']");
    private final SelenideElement categoryRequiredFieldHint =
            categorySelect.getElement().$x(".//div[text()='" + requiredFieldText + "']");
    private final SelenideElement categoryV2RequiredFieldHint =
            categoryV2Select.getElement().$x(".//div[text()='" + requiredFieldText + "']");
    private final SelenideElement authorRequiredFieldHint =
            authorInput.getInput().$x("ancestor::div[2]//div[text()='" + requiredFieldText + "']");

    public ProductPage() {
        productsListLink.shouldBe(Condition.visible);
    }

    @Step("Заполнение атрибутов продукта '{product.name}'")
    public ProductPage setAttributes(Product product) {
        titleInput.setValue(product.getTitle());
        nameInput.setValue(product.getName());
        authorInput.setValue(product.getAuthor());
        descriptionInput.setValue(product.getDescription());
        info.setValue(new JSONObject(product.getInfo()).toString());
        isOpenSwitch.getLabel().scrollIntoView(scrollCenter);
        isOpenSwitch.setEnabled(product.getIsOpen());
        goToGraphTab();
        Graph graph = GraphSteps.getGraphById(product.getGraphId());
        graphSelect.setContains(graph.getName());
        Waiting.find(() -> graphSelect.getValue().contains(graph.getName()), Duration.ofSeconds(10));
        graphVersionSelect.set(product.getGraphVersion());
        Waiting.find(() -> graphVersionSelect.getValue().equals(product.getGraphVersion()), Duration.ofSeconds(3));
        goToAdditionalParamsTab();
        categorySelect.set(Categories.VM.getValue());
        Waiting.sleep(500);
        categoryV2Select.set(Categories.COMPUTE.getValue());
        maxCountInput.setValue(product.getMaxCount());
        if (product.getOnRequest() != null) onRequestSelect.set(product.getOnRequest().getDisplayName());
        inGeneralListSwitch.setEnabled(product.getInGeneralList());
        numberInput.setValue(product.getNumber());
        extraData.setValue(new JSONObject(product.getExtraData()).toString());
        return this;
    }

    @Step("Проверка атрибутов продукта '{product.name}'")
    public ProductPage checkAttributes(Product product) {
        checkVersion(product.getVersion());
        goToMainTab();
        nameInput.getInput().shouldHave(Condition.exactValue(product.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(product.getTitle()));
        authorInput.getInput().shouldHave(Condition.exactValue(product.getAuthor()));
        descriptionInput.getInput().shouldHave(Condition.exactValue(product.getDescription()));
        assertEquals(new JSONObject(product.getInfo()).toString(),
                info.getWhitespacesRemovedValue());
        assertEquals(product.getIsOpen(), isOpenSwitch.isEnabled());
        goToGraphTab();
        Graph graph = GraphSteps.getGraphById(product.getGraphId());
        Waiting.find(() -> graphSelect.getValue().contains(graph.getName()), Duration.ofSeconds(3),
                "Название графа не содержит " + graph.getName());
        assertEquals(product.getGraphVersion(), graphVersionSelect.getValue());
        goToAdditionalParamsTab();
        assertEquals(product.getCategory(), categorySelect.getValue());
        assertEquals(product.getCategoryV2().getValue(), categoryV2Select.getValue());
        maxCountInput.getInput().shouldHave(Condition.exactValue(String.valueOf(product.getMaxCount())));
        assertEquals(product.getOnRequest() == null ? "Нет" : product.getOnRequest().getDisplayName(),
                onRequestSelect.getValue());
        assertEquals(product.getInGeneralList(), inGeneralListSwitch.isEnabled());
        numberInput.getInput().shouldHave(Condition.exactValue(String.valueOf(product.getNumber())));
        assertEquals(new JSONObject(product.getExtraData()).toString(),
                extraData.getWhitespacesRemovedValue());
        return this;
    }

    @Step("Проверка обязательных полей при создании продукта")
    public ProductsListPage checkRequiredFields(Product product) {
        descriptionInput.setValue("test");
        saveButton.getButton().shouldBe(Condition.disabled);
        nameRequiredFieldHint.shouldBe(Condition.visible);
        nameInput.setValue(product.getName());
        nameRequiredFieldHint.shouldNotBe(Condition.visible);
        titleRequiredFieldHint.shouldBe(Condition.visible);
        titleInput.setValue(product.getTitle());
        titleRequiredFieldHint.shouldNotBe(Condition.visible);
        authorRequiredFieldHint.shouldBe(Condition.visible);
        authorInput.setValue(product.getAuthor());
        authorRequiredFieldHint.shouldNotBe(Condition.visible);
        goToGraphTab();
        graphRequiredFieldHint.shouldBe(Condition.visible);
        graphSelect.setContains(GraphSteps.getGraphById(product.getGraphId()).getName());
        graphRequiredFieldHint.shouldNotBe(Condition.visible);
        goToAdditionalParamsTab();
        categoryRequiredFieldHint.shouldBe(Condition.visible);
        categorySelect.set(product.getCategory());
        categoryRequiredFieldHint.shouldNotBe(Condition.visible);
        categoryV2RequiredFieldHint.shouldBe(Condition.visible);
        categoryV2Select.set(product.getCategoryV2().getValue());
        categoryV2RequiredFieldHint.shouldNotBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.enabled);
        cancelButton.click();
        Selenide.prompt();
        return new ProductsListPage();
    }

    @Step("Задание версии графа '{version}'")
    public ProductPage setGraphVersion(String version) {
        goToGraphTab();
        Waiting.sleep(2000);
        graphVersionSelect.set(version);
        return this;
    }

    @Step("Задание в поле Автор '{value}'")
    public ProductPage setAuthor(String value) {
        goToMainTab();
        authorInput.setValue(value);
        return this;
    }

    @Step("Сохранение продукта без патч-версии")
    public ProductPage saveWithoutPatchVersion(String alertText) {
        super.saveWithoutPatchVersion(alertText);
        return new ProductPage();
    }

    @Step("Задание значения в поле 'Описание'")
    public ProductPage setDescription(String value) {
        descriptionInput.setValue(value);
        return this;
    }

    @Step("Проверка, что отображаемая версия равна '{version}'")
    public ProductPage checkVersion(String version) {
        this.selectedVersion.scrollIntoView(false).shouldHave(Condition.exactText(version));
        return this;
    }

    @Step("Проверка сохранения сервиса с некорректной версией '{newVersion}'")
    public ProductPage checkSaveWithInvalidVersion(String newVersion, String currentVersion) {
        super.checkSaveWithInvalidVersion(newVersion, currentVersion);
        return this;
    }

    @Step("Проверка сохранения сервиса с версией некорректного формата '{newVersion}'")
    public ProductPage checkSaveWithInvalidVersionFormat(String newVersion) {
        super.checkSaveWithInvalidVersionFormat(newVersion);
        return this;
    }

    public ProductPage goToMainTab() {
        goToTab("Основное");
        return this;
    }

    public ProductPage goToGraphTab() {
        goToTab("Граф");
        return this;
    }

    public ProductPage goToAdditionalParamsTab() {
        goToTab("Дополнительные параметры");
        return this;
    }

    @Step("Переход на вкладку 'Сравнение версий'")
    public ProductPage goToVersionComparisonTab() {
        goToTab("Сравнение");
        return this;
    }

    @Step("Проверка валидации недопустимых значений в коде продукта")
    public ProductsListPage checkNameValidation(String[] names) {
        for (String name : names) {
            nameInput.setValue(name);
            Waiting.findWithAction(() -> nameValidationHint.isDisplayed(),
                    () -> nameInput.getInput().sendKeys("t"), Duration.ofSeconds(3));
            nameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return new ProductsListPage();
    }

    @Step("Проверка валидации неуникального имени продукта '{product.name}'")
    public ProductsListPage checkNonUniqueNameValidation(Product product) {
        nameInput.setValue(product.getName());
        titleInput.setValue(product.getTitle());
        nonUniqueNameValidationHint.shouldBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return new ProductsListPage();
    }

    @Step("Удаление продукта")
    public void delete() {
        deleteButton.click();
        new DeleteDialog().submitAndDelete("Удаление выполнено успешно");
    }

    @Step("Сохранение продукта со следующей патч-версией")
    public ProductPage saveWithPatchVersion() {
        super.saveWithPatchVersion(saveProductAlertText);
        return this;
    }

    @Step("Сохранение продукта с версией '{newVersion}'")
    public ProductPage saveWithManualVersion(String newVersion) {
        super.saveWithManualVersion(newVersion, saveProductAlertText);
        return this;
    }

    @Step("Проверка, что следующая предлагаемая версия для сохранения равна '{nextVersion}' и сохранение")
    public ProductPage checkNextVersionAndSave(String nextVersion) {
        super.checkNextVersionAndSave(nextVersion, saveProductAlertText);
        return this;
    }

    @Step("Удаление иконки")
    public ProductPage deleteIcon() {
        deleteIconButton.scrollIntoView(scrollCenter).click();
        saveWithoutPatchVersion(saveProductAlertText);
        addIconLabel.shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка недоступности удаления открытого продукта")
    public void checkDeleteOpenProduct() {
        isOpenSwitch.getLabel().scrollIntoView(true);
        isOpenSwitch.setEnabled(true);
        deleteButton.getButton().hover();
        assertEquals("Недоступно для открытого продукта", new Tooltip().toString());
        deleteButton.getButton().shouldBe(Condition.disabled);
        isOpenSwitch.setEnabled(false);
        deleteButton.getButton().hover();
        Assertions.assertFalse(Tooltip.isVisible());
        deleteButton.getButton().shouldBe(Condition.enabled);
    }

    @Step("Возврат в список продуктов по хлебным крошкам")
    public ProductsListPage goToProductsList() {
        productsListLink.scrollIntoView(false).click();
        return new ProductsListPage();
    }

    @Step("Возврат в список продуктов по кнопке Назад")
    public ProductsListPage backToProductsList() {
        backButton.click();
        return new ProductsListPage();
    }

    @Step("Проверка баннера о несохранённых изменениях. Отмена")
    public ProductPage checkUnsavedChangesAlertDismiss() {
        String newValue = "new";
        goToMainTab();
        titleInput.setValue(newValue);
        back();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        productsListLink.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        backButton.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        mainPage.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        return this;
    }

    @Step("Проверка баннера о несохранённых изменениях. Ок")
    public ProductPage checkUnsavedChangesAlertAccept(Product product) {
        String newValue = "new title";
        goToMainTab();
        titleInput.setValue(newValue);
        back();
        acceptAlert(unsavedChangesAlertText);
        new ProductsListPage().openProductPage(product.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(product.getTitle()));
        titleInput.setValue(newValue);
        productsListLink.click();
        acceptAlert(unsavedChangesAlertText);
        new ProductsListPage().openProductPage(product.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(product.getTitle()));
        titleInput.setValue(newValue);
        backButton.click();
        acceptAlert(unsavedChangesAlertText);
        new ProductsListPage().openProductPage(product.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(product.getTitle()));
        titleInput.setValue(newValue);
        mainPage.click();
        acceptAlert(unsavedChangesAlertText);
        new ControlPanelIndexPage().goToProductsListPage().openProductPage(product.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(product.getTitle()));
        return this;
    }
}
