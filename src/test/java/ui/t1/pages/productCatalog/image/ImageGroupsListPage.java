package ui.t1.pages.productCatalog.image;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.t1.imageService.ImageGroup;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;
import ui.cloud.pages.productCatalog.BaseListPage;
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
    private final DropDown logoDropDown = DropDown.byXpath("//div[@data-field-name='logo_id']");
    private final Input nameInput = Input.byName("name");
    private final Input titleInput = Input.byName("title");
    private final Input tagsInput = Input.byPlaceholder("Введите теги через запятую и нажмите Enter");
    private final Input distroInput = Input.byName("distro");
    private final Button addButton = Button.byText("Добавить");
    private final Button cancelButton = Button.byText("Отменить");

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

    public ImageGroupsListPage addImageGroup(ImageGroup imageGroup) {
        addNewObjectButton.click();
        logoDropDown.selectByDivText("ubuntu");
        nameInput.setValue(imageGroup.getName());
        titleInput.setValue(imageGroup.getTitle());
        for (Object tag : imageGroup.getTags()) {
            tagsInput.setValue(tag);
            tagsInput.getInput().sendKeys(Keys.ENTER);
        }
        distroInput.setValue(imageGroup.getDistro());
        addButton.click();
        //Alert.green("Группа образов успешно добавлена");
        return this;
    }

    public ImageGroupsListPage editImageGroup(ImageGroup imageGroup) {
        openEditDialog(imageGroup);
        checkAttribures(imageGroup);
        return this;
    }

    public ImageGroupsListPage openEditDialog(ImageGroup imageGroup) {
        Table table = new Table(titleColumn);
        table.getRowByColumnValueContains(nameColumn, imageGroup.getName()).get()
                .$x(".//td[last()-1]//*[@class and name()='svg']").click();
        return this;
    }

    public ImageGroupsListPage checkAttribures(ImageGroup imageGroup) {
        Assertions.assertEquals(imageGroup.getName(), nameInput.getValue());
        Assertions.assertEquals(imageGroup.getTitle(), titleInput.getValue());
        Assertions.assertEquals(imageGroup.getDistro(), distroInput.getValue());
        Assertions.assertEquals(imageGroup.getLogoId(), logoDropDown.getValue());
        for (Object tag : imageGroup.getTags()) {
            $x("//form//span[text()='{}']", tag).shouldBe(Condition.visible);
        }
        cancelButton.click();
        return this;
    }
}
