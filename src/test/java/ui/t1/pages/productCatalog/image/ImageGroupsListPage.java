package ui.t1.pages.productCatalog.image;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.t1.imageService.ImageGroup;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.*;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImageGroupsListPage extends BaseListPage {

    private final String nameColumn = "Имя";
    private final String titleColumn = "Название";
    private final String osVersionColumn = "Версия ОС";
    private final SelenideElement pageTitle = $x("//div[text() = 'Группы образов']");
    private final Select logoSelect = Select.byLabel("Логотип");
    private final Input nameInput = Input.byName("name");
    private final Input titleInput = Input.byName("title");
    private final Input tagsInput = Input.byPlaceholder("Введите теги через запятую и нажмите Enter");
    private final Input distroInput = Input.byName("distro");
    private final Button addButton = Button.byText("Добавить");
    private final Button saveButton = Button.byText("Сохранить");
    private final Button cancelButton = Button.byText("Отменить");
    private final SelenideElement tagElement = $x("//form//div[@role='button']");

    public ImageGroupsListPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    @Step("Проверка заголовков списка групп образов")
    public ImageGroupsListPage checkImageGroupsListHeaders() {
        Table table = new Table(titleColumn);
        assertEquals(Arrays.asList("Имя", "Название", "Теги", "Дистрибутив", "Дата и время синхронизации"),
                table.getNotEmptyHeaders());
        return this;
    }

    @Step("Изменение количества отображаемых строк")
    public ImageGroupsListPage setRecordsPerPage(int number) {
        super.setRecordsPerPage(number);
        Table imagesTable = new Table(titleColumn);
        Assertions.assertTrue(imagesTable.getRows().size() <= number);
        return this;
    }

    @Step("Раскрытие информации по группе образов")
    public ImageGroupsListPage showImageGroupInfo(int rowNumber) {
        Table table = new Table(titleColumn);
        SelenideElement row = table.getRow(rowNumber).get();
        row.$x(".//td[last()]//button").click();
        Table imageGroupInfoTable = new Table(osVersionColumn);
        assertEquals(Arrays.asList("Имя", "Дистрибутив", "Версия ОС", "Теги", "ID"),
                imageGroupInfoTable.getNotEmptyHeaders());
        return this;
    }

    @Step("Скрытие информации по группе образов")
    public ImageGroupsListPage hideImageGroupInfo(int rowNumber) {
        Table table = new Table(titleColumn);
        SelenideElement row = table.getRow(rowNumber).get();
        row.$x(".//td[last()]//button").click();
        row.$x("following::th[text()='" + osVersionColumn + "']").shouldNotBe(Condition.visible);
        return this;
    }

    @Step("Добавление группы образов '{imageGroup.name}'")
    public ImageGroupsListPage addImageGroup(ImageGroup imageGroup) {
        addNewObjectButton.click();
        logoSelect.set("ubuntu");
        nameInput.setValue(imageGroup.getName());
        titleInput.setValue(imageGroup.getTitle());
        setTags(imageGroup);
        distroInput.setValue(imageGroup.getDistro());
        addButton.click();
        Alert.green("Группа образов успешно добавлена");
        return this;
    }

    @Step("Редактирование группы образов '{imageGroup.name}'")
    public ImageGroupsListPage editImageGroup(ImageGroup imageGroup) {
        openEditDialog(imageGroup);
        nameInput.setValue(imageGroup.getName());
        titleInput.setValue(imageGroup.getTitle());
        distroInput.setValue(imageGroup.getDistro());
        clearAllTags();
        setTags(imageGroup);
        saveButton.click();
        Alert.green("Данные успешно обновлены");
        return this;
    }

    @Step("Удаление группы '{imageGroup.name}'")
    public ImageGroupsListPage delete(ImageGroup imageGroup) {
        Table table = new Table(titleColumn);
        table.getRowByColumnValue(nameColumn, imageGroup.getName()).get()
                .$x(".//td[last()-1]//*[@class and name()='svg']").click();
        new DeleteDialog().inputValidIdAndDelete("Группа успешно удалена");
        Assertions.assertFalse(table.isColumnValueEquals(nameColumn, imageGroup.getName()));
        return this;
    }

    @Step("Открытие формы редактирования для группы '{imageGroup.name}'")
    public ImageGroupsListPage openEditDialog(ImageGroup imageGroup) {
        Table table = new Table(titleColumn);
        table.getRowByColumnValueContains(nameColumn, imageGroup.getName()).get()
                .$x(".//td[last()-2]//*[@class and name()='svg']").click();
        return this;
    }

    @Step("Проверка аттрибутов группы '{imageGroup.name}'")
    public ImageGroupsListPage checkAttributes(ImageGroup imageGroup) {
        openEditDialog(imageGroup);
        Assertions.assertEquals(imageGroup.getName(), nameInput.getValue());
        Assertions.assertEquals(imageGroup.getTitle(), titleInput.getValue());
        Assertions.assertEquals(imageGroup.getDistro(), distroInput.getValue());
        String logoUrl = logoSelect.getElement().$x(".//img").getAttribute("src");
        Assertions.assertEquals(imageGroup.getLogo(), logoUrl);
        for (Object tag : imageGroup.getTags()) {
            $x("//form//span[text()='{}']", tag).shouldBe(Condition.visible);
        }
        cancelButton.click();
        return this;
    }

    @Step("Добавление тегов")
    private void setTags(ImageGroup imageGroup) {
        for (Object tag : imageGroup.getTags()) {
            tagsInput.setValue(tag);
            tagsInput.getInput().sendKeys(Keys.ENTER);
        }
    }

    @Step("Удаление всех тегов")
    private void clearAllTags() {
        while (tagElement.exists()) {
            tagElement.$x(".//*[name()='svg']").click();
        }
    }
}
