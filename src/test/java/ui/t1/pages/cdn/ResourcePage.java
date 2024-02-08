package ui.t1.pages.cdn;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import ui.elements.*;

import java.util.List;
import java.util.stream.IntStream;

import static core.helper.StringUtils.$x;

public class ResourcePage {

    private final Button editButton = Button.byText("Редактировать");
    private final Menu menu = Menu.byElement(Selenide.$x("//*[@id='actions-menu-button']"));
    private final Dialog cacheDialog = Dialog.byTitle("Очистить кэш");
    private final Button saveButton = Button.byText("Сохранить");
    private final Table contentTable = new Table(Selenide.$x("//span[text()='Контент']/following-sibling::div/div/table"));
    private final Dialog editDialog = Dialog.byTitle("Редактировать ресурс");

    public ResourcePage(String resourceName) {
        $x("//span[text() = '{}']", resourceName).shouldBe(Condition.visible);
    }

    @Step("Редактирование доменных имен ресурса")
    public ResourcePage editResourceHostNames(List<String> hostnames) {
        editButton.click();
        int hostNameSize = hostnames.size();
        IntStream.range(0, hostNameSize).forEach(i -> {
            editDialog.clickButton("Добавить");
            editDialog.setInputByName(String.format("hostnames-%s", i + 1), hostnames.get(i));
        });
        saveButton.click();
        Alert.green("Ресурс изменён");
        return this;
    }

    @Step("[Проверка] Колонка 'Дополнительные доменные имена' содержит доменные имена: {0}")
    public ResourcePage checkDomainsColumnHasNames(List<String> domainName) {
        domainName.forEach(domain -> contentTable.update().getRowByColumnIndex(0, "Дополнительные доменные имена")
                .asserts().checkLastValueOfRowContains(domain));
        return this;
    }

    @Step("Полное очищение кэша")
    public ResourcePage fullResetCache() {
        menu.select("Очистить кэш");
        cacheDialog.setRadio(Radio.byValue("Полная"));
        cacheDialog.clickButton("Очистить");
        return this;
    }

    @Step("Выборочное очищение кэша")
    public ResourcePage partialResetCache() {
        menu.select("Очистить кэш");
        cacheDialog.setRadio(Radio.byValue("Выборочная"));
        cacheDialog.setTextarea(TextArea.byLabel("Пути к файлам"), "/foo.css");
        cacheDialog.clickButton("Очистить");
        return this;
    }
}
