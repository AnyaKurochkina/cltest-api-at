package ui.t1.pages.supportCenter;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Keys;
import ui.elements.Button;
import ui.elements.Select;
import ui.elements.Table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.switchTo;
import static core.enums.NotificationCenterPriorities.HIGH;
import static core.enums.NotificationCenterPriorities.LOW;
import static core.helper.StringUtils.$$x;
import static core.helper.StringUtils.$x;


public class NotificationsPage {

    SelenideElement themeGroupFirstRow = $x("(//span[contains(@class, 'styles__GroupTheme')]) [2]");

    Button numberOfRows = Button.byXpath("//button[starts-with(@aria-label,'Записей')]");
    Button topBarNotification = Button.byDataTestId("topbar-menu-notification");



    String groupHeader = "Группа тем";
    String descriptionHeader = "Описание";
    String contextHeader = "Контекст";
    String dateHeader = "Дата создания";
    String priorityHeader = "Приоритет";
    @Step("Проверяем название группы тем у сообщения")
    public NotificationsPage checkThemeGroup(String themeGroupName){
        Assertions.assertEquals(themeGroupName, themeGroupFirstRow.text());
        return this;
    }

    @Step("Проверяем наличие метки 'непрочитано'")
    public NotificationsPage checkUnReadMark(){
        themeGroupFirstRow.shouldNotHave(Condition.pseudo(
                ":before",
                "display",
                "none"));
        return this;
    }

    @Step("Проверяем отсутствие метки 'непрочитано'")
    public NotificationsPage checkNoReadMark(){
        themeGroupFirstRow.shouldHave(Condition.pseudo(
                ":before",
                        "display",
                        "none"));
        return this;
    }

    @Step("Проверяем текст описания")
    public NotificationsPage checkDescription(String description){
        Assertions.assertEquals(description,
        $x("(//span[contains(@class, 'styles__Description')]) [1]").text());
        return this;
    }

    @Step("Проверяем контекст")
    public NotificationsPage checkContext(String context){
        Assertions.assertEquals(context,
                $x("(//div[contains(@class, 'styles__Context')]) [1]").text());
        return this;
    }

    @Step("Проверяем приоритет")
    public NotificationsPage checkImportance(String importance){
        String temp;
        switch (importance){
            case ("HIGH") :
                temp = "Высокий";
                break;
            case ("LOW") :
                temp = "Низкий";
                break;
            default:
                temp = "Средняя";
                break;
        }
        Assertions.assertEquals(temp,
                $x("(//span[contains(@class, 'Importance')]) [1]").text());
        return this;
    }

    @Step("Проверяем дату")
    public NotificationsPage checkDate(String date){
        Assertions.assertEquals(date,
                $x("(//span[contains(@class, 'MuiTypography-root')]) [1]").text() + " " +
                $x("(//span[contains(@class, 'MuiTypography-root')]) [2]").text());
        return this;
    }

    @Step("Кликаем на Развернуть сообщение")
    public NotificationsPage clickExpandButton(){
        $x("(//div[contains(@class, 'Expand')]) [1]").click();
        return this;
    }

    @Step("Проверяем тему в раскрытом сообщении")
    public NotificationsPage checkExpandedMessage(String subject){
        Assertions.assertEquals(subject,
        $x("(//span[contains(@class, 'ThemeStyled')])").text());
        return this;
    }

    @Step("Проверяем ссылку")
    public NotificationsPage checkLink(){
        $x("//a[.='Подробнее']").click();
        switchTo().window(1);
        Assertions.assertEquals("T1 Disk",
                $x("(//div[contains(@class, 'HeaderStyled')]) [2]").text());
        switchTo().window(0);
        return this;
    }

    @Step("Проверяем полученное сообщение")
    public NotificationsPage checkMessage(String date, String groupName, String description){
        Table table = new Table("Группа тем");
        Table.Row row = table.getRowByColumnValueContains("Дата создания", date);
        Assertions.assertEquals(groupName, row.getValueByColumn(groupHeader));
        Assertions.assertEquals(description, row.getValueByColumn(descriptionHeader));
        Assertions.assertEquals("Глобальный", row.getValueByColumn(contextHeader));
        Assertions.assertEquals(HIGH.getUiName(), row.getValueByColumn(priorityHeader));
        return this;

    }

    @Step("Проверяем заголовки таблицы")
    public NotificationsPage checkTableHeaders(){
        $x("//table/thead/tr/th[.='" + groupHeader + "']").shouldBe(Condition.visible);
        $x("//table/thead/tr/th[.='" + descriptionHeader +"']").shouldBe(Condition.visible);
        $x("//table/thead/tr/th[.='" + contextHeader + "']").shouldBe(Condition.visible);
        $x("//table/thead/tr/th[.='" + dateHeader + "']").shouldBe(Condition.visible);
        $x("//table/thead/tr/th[.='" + priorityHeader + "']").shouldBe(Condition.visible);
        return this;
    }

    @Step("Нажимаем 'Фильтры'")
    public NotificationsPage clickFilters(){
        $x("(//div[.='Фильтры']) [4]").click();
        return this;
    }

    @Step("Задаем значение {value} для фильтра Группа тем")
    public NotificationsPage setThemeGroupFilter(String value){
        Select.byLabel("Группа тем").set(value);
        return this;
    }

    @Step("Задаем значение {value} для фильтра Приоритет")
    public NotificationsPage setPriorityFilter(String value){
        Select.byLabel("Приоритет").set(value);
        return this;
    }

    @Step("Выставляем фильтр Прочитано")
    public NotificationsPage setRead(){
        Select.byLabel("Прочитано").set("Прочитанные");
        return this;
    }

    @Step("Выставляем фильтр Непрочитано")
    public NotificationsPage setUnRead(){
        Select.byLabel("Прочитано").set("Непрочитанные");
        return this;
    }

    @Step("Нажимаем кнопку Применить")
    public NotificationsPage clickApply(){
        Button.byText("Применить").click();
        return this;
    }

    @Step("Проверяем что на странице {size} уведомлений")
    public NotificationsPage checkNumberOfNotifications(int size){
        $x("//table/thead/tr/th[.='" + groupHeader + "']").shouldBe(Condition.visible);
        $$x("(//span[contains(@class, 'styles__GroupTheme')])").shouldHave(CollectionCondition.size(size + 1));
        //1 заложено на строку с заголовками
        return this;
    }

    @Step("Нажимаем Отметить всё как прочитанное")
    public NotificationsPage clickMarkAllRead(){
        Button.byText("Отметить всё как прочитанное").click();
        return this;
    }

    @Step("Нажимаем Сбросить фильтры")
    public NotificationsPage clickResetFilters(){
        Button.byText("Сбросить фильтры").click();
        return this;
    }

    @Step("Заполняем фильтр Период")
    public NotificationsPage setDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate yesterdayDate = LocalDate.now().minusDays(1);
        LocalDate tomorrowDate = LocalDate.now().plusDays(1);
        $x("//label[starts-with(.,'Период')]/following::input[1]")
                .sendKeys(Keys.HOME + yesterdayDate.format(formatter) + tomorrowDate.format(formatter));


        return this;
    }

    @Step("Проверяем что в колонке Дата создания нет других дат, кроме сегодняшней")
    public NotificationsPage checkDateColumn(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate todayDate = LocalDate.now();
        Table table = new Table(dateHeader);
        for (int i = 0; i < 10; i++){
            table.getValueByColumnInRow(i, dateHeader).shouldHave(Condition.text(todayDate.format(formatter)));
        }
        return this;
    }

    @Step("Проверяем что в колонке Приоритет нет других значение кроме Низкий")
    public NotificationsPage checkPriorityColumn(){
        Table table = new Table(priorityHeader);
        for(int i = 0; i < 5; i++){
            table.getValueByColumnInRow(i, priorityHeader).shouldHave(Condition.text(LOW.getUiName()));
        }
        return this;
    }

    @Step("Переключаем количество записей на странице на {value}")
    public NotificationsPage setNumberOfRows(int value){
        numberOfRows.click();
        $x("//div[@role='listbox']").shouldBe(Condition.visible);
        $x("//div[@role='listbox']//div[contains(text(), '{}')]", value).click();
        return this;
    }

    @Step("Нажимаем следующая страница и проверяем, что страница переключилась")
    public NotificationsPage clickNextPage(){
        Button.byAriaLabel("Следующая страница, выбрать").click();
        $x("//button[starts-with(@aria-label,'Страница 2')]").shouldBe(Condition.visible);
        return this;
    }

    @Step("Нажимаем на колокольчик")
    public NotificationsPage clickTopBarNotification(){
        topBarNotification.click();
        $x("//a[.='Посмотреть все уведомления']").shouldBe(Condition.visible);
        return this;
    }

    @Step("Сверяем количество непрочитанных сообщений в счетчике на колокольчике")
    public NotificationsPage checkUnreadCounter(int value){
        if (value > 0){
            $x("//div[starts-with(@aria-label, 'Количество')]", value).shouldBe(Condition.visible);
            $x("//div[@aria-label='Количество {}']", value).shouldHave(Condition.text(String.valueOf(value)));
        }
        else {
            $x("//div[starts-with(@aria-label, 'Количество')]", value).shouldNotBe(Condition.visible);
        }
        return this;
    }


    @Step("Проверяем что в колокольчике отображаются правильное количество сообщений")
    public NotificationsPage checkNumberOfMessagesTopBar(String header, int size){
        $$x("//span[.='{}']", header).shouldHave(CollectionCondition.size(size));
        return this;
    }

    @Step("Кликаем на сообщение в колокольчике и проверяем переход в Мои уведомления")
    public NotificationsPage clickMessage(String header){
        $x("(//span[.='{}'])[1]", header).click();
        $x("//*[.='Мои уведомления']").shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверяем наличие меток Важно в колокольчике")
    public NotificationsPage checkImportant(){
        $x("//div[.='Тест колокольчика']/*[name()='svg']").shouldBe(Condition.visible);
        $$x("//div[.='Тест колокольчика']/*[name()='svg']").shouldHave(CollectionCondition.size(5));
        return this;
    }

    @Step("Проверяем что меток Важно нет в колокольчике")
    public NotificationsPage checkNoImportant(){
        $x("//div[.='Тест колокольчика']/*[name()='svg']").shouldNotBe(Condition.visible);
        return this;
    }

    @Step("Проверяем правильность заголовка в колокольчике")
    public NotificationsPage checkMessageHeader(String subject){
        $x("//div[.='{}']/span", subject).shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверяем ссылку в колокольчике")
    public NotificationsPage checkMessageLink(){
        $x("//a[.='Подробнее']").click();
        switchTo().window(1);
        Assertions.assertEquals("T1 Disk",
                $x("(//div[contains(@class, 'HeaderStyled')]) [2]").text());


        return this;
    }

    @Step("Закрываем колокольчик")
    public NotificationsPage closeTopMessage(){
        switchTo().window(0);
        $x("//a[.='Посмотреть все уведомления']").pressEscape();
        return this;
    }

    @Step("Открываем и закрываем спойлер сообщения в ЦУ")
    public NotificationsPage clickMessageTwice(){
        $x("(//td//button) [2]").click();
        $x("(//td//button) [2]").click();

        return this;

    }
}
