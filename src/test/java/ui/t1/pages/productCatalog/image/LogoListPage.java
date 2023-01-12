package ui.t1.pages.productCatalog.image;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.t1.imageService.ImageGroup;
import models.t1.imageService.Logo;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Input;
import ui.elements.Table;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogoListPage extends BaseListPage {

    private final String nameColumn = "Имя";
    private final SelenideElement pageTitle = $x("//div[text() = 'Логотипы']");
    private final Input nameInput = Input.byName("name");
    private final Input urlInput = Input.byName("logo");
    private final Input distroInput = Input.byName("os_distro");
    private final Button addButton = Button.byText("Добавить");
    private final Button cancelButton = Button.byText("Отменить");
    private final SelenideElement nameRequiredFieldText
            = nameInput.getInput().$x("following::div[text()='Поле обязательно для заполнения']");
    private final SelenideElement distroRequiredFieldText
            = distroInput.getInput().$x("following::div[text()='Поле обязательно для заполнения']");
    private final SelenideElement urlIncorrectFormatText
            = urlInput.getInput().$x("following::div[text()='Введите валидный url адрес логотипа']");

    public LogoListPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    @Step("Проверка заголовков списка логотипов")
    public LogoListPage checkHeaders() {
        Table table = new Table(nameColumn);
        assertEquals(Arrays.asList("Имя", "Дистрибутив"),
                table.getNotEmptyHeaders());
        return this;
    }

    @Step("Изменение количества отображаемых строк")
    public LogoListPage setRecordsPerPage(int number) {
        super.setRecordsPerPage(number);
        Table table = new Table(nameColumn);
        Assertions.assertTrue(table.getRows().size() <= number);
        return this;
    }

    @Step("Добавление логотипа '{logo.name}'")
    public LogoListPage createLogo(Logo logo) {
        addNewObjectButton.click();
        nameInput.setValue(logo.getName());
        urlInput.setValue(logo.getLogo());
        $x("//div[@role='dialog']//img[@src='{}']", logo.getLogo()).shouldBe(Condition.visible);
        distroInput.setValue(logo.getOsDistro());
        addButton.click();
        Alert.green("Логотип успешно добавлен");
        return this;
    }

    @Step("Проверка обязательных полей при добавлении логотипа")
    public LogoListPage checkRequiredFields(Logo logo) {
        addNewObjectButton.click();
        nameInput.setValue(logo.getName());
        urlInput.setValue(logo.getLogo());
        distroInput.setValue(logo.getOsDistro());
        if (logo.getName().isEmpty()) {
            nameRequiredFieldText.shouldBe(Condition.visible);
        }
        if (logo.getLogo().isEmpty()) {
            urlIncorrectFormatText.shouldBe(Condition.visible);
        }
        if (logo.getOsDistro().isEmpty()) {
            distroRequiredFieldText.shouldBe(Condition.visible);
        }
        addButton.getButton().shouldNotBe(Condition.enabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации поля URL логотипа")
    public LogoListPage checkUrlValidation(Logo logo) {
        addNewObjectButton.click();
        nameInput.setValue(logo.getName());
        urlInput.setValue(logo.getLogo());
        distroInput.setValue(logo.getOsDistro());
        urlIncorrectFormatText.shouldBe(Condition.visible);
        addButton.getButton().shouldNotBe(Condition.enabled);
        cancelButton.click();
        return this;
    }

    @Step("Открытие формы редактирования для логотипа '{logo.name}'")
    public LogoListPage openEditDialog(Logo logo) {
        Table table = new Table(nameColumn);
        table.getRowByColumnValueContains(nameColumn, logo.getName()).get()
                .$x(".//td[last()]//*[@class and name()='svg']").click();
        return this;
    }

    @Step("Проверка аттрибутов логотипа '{logo.name}'")
    public LogoListPage checkAttributes(Logo logo) {
        openEditDialog(logo);
        Assertions.assertEquals(logo.getName(), nameInput.getValue());
        Assertions.assertEquals(logo.getLogo(), urlInput.getValue());
        $x("//div[@role='dialog']//img[@src='{}']", logo.getLogo()).shouldBe(Condition.visible);
        Assertions.assertEquals(logo.getOsDistro(), distroInput.getValue());
        cancelButton.click();
        return this;
    }
}
