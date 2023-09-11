package ui.t1.pages.supportCenter;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

import static core.helper.StringUtils.$x;

public class MySubscriptionsPage {
    String tableHeader = "Группа тем";
    SelenideElement setPage = $x("//button[starts-with(@aria-label,'Страница')]");
    String actionColumn = "Действие";
    String contextColumn = "Контекст";
    String themeColumn = "Тема";
    String globalContext = "Глобальный";



    @Step("Нажимаем Создать подписку")
    public MySubscriptionsPage clickCreateSubscription(){
        new DataTable(contextColumn).clickAdd();
        return this;
    }

    @Step("Нажимаем Глобальные")
    public MySubscriptionsPage clickGlobal(){
        Button.byText("Глобальные").click();
        return this;
    }

    @Step("Переходим на страницу {page}")
    public MySubscriptionsPage setPage(int page){
        new DataTable(tableHeader).selectPage(page);
        return this;
    }

    @Step("Нажимаем 'Подписаться' напротив Группы тем {groupName}")
   public MySubscriptionsPage clickSubscribeThemeGroup(String groupName) {
        DataTable themeGroupsTable = new DataTable(tableHeader);
        themeGroupsTable.searchAllPages(t-> themeGroupsTable.isColumnValueEquals(tableHeader, groupName))
                .getRowByColumnValue(tableHeader, groupName)
                .getElementByColumn(actionColumn).$x("..//button").click();
            Alert.green("Подписки успешно созданы");

            return this;
    }

    @Step("Нажимаем 'Отписаться' напротив Группы тем {groupName}")
    public MySubscriptionsPage clickUnSubscribeThemeGroup(String groupName){
        DataTable themeGroupsTable = new DataTable(tableHeader);
        themeGroupsTable.searchAllPages(t-> themeGroupsTable.isColumnValueEquals(tableHeader, groupName))
                .getRowByColumnValue(tableHeader, groupName)
                .getElementByColumn(actionColumn).$x("..//button").click();
            Dialog.byTitle("Подтверждение удаления подписок на группу тем").clickButton("Да");
            Alert.green("Подписки удалены");

            return this;
    }

    @Step("Подписываемся на группу тем {groupName}")
    public MySubscriptionsPage createThemeGroupSubscription(String groupName){
        clickCreateSubscription();
        clickGlobal();
        clickSubscribeThemeGroup(groupName);

        return this;
    }

    @Step("Проверяем что Группа тем появилась в подписках")
    public MySubscriptionsPage checkThemeGroupSubscription(String context, String ... themes){
        returnToMySubscriptionPage();
        clickContextButton(context);
        for(String theme : themes){
            Table table = new Table(tableHeader);
            Assertions.assertTrue(table.isColumnValueEquals("Тема", theme));
        }
        return this;
    }

    @Step("Раскрываем спойлер контекста")
    public MySubscriptionsPage clickContextButton(String context){
        DataTable mySubscriptions = new DataTable(contextColumn);
        mySubscriptions.searchAllPages(t -> mySubscriptions.isColumnValueEquals(contextColumn, context))
                .getRowByColumnValue(contextColumn, context)
                .getElementLastColumn().$x("..//button").click();
        return this;
    }

    @Step("Возвращаемся к Мои подписки")
    public MySubscriptionsPage returnToMySubscriptionPage(){
        $x("//div[.='Мои подписки']").click();
        return this;
    }

    @Step("Проверяем что тема появилась в подписках")
    public MySubscriptionsPage checkThemeSubscription(String theme, String context){
        returnToMySubscriptionPage();
        clickContextButton(context);
      Assertions.assertTrue(new Table(tableHeader).isColumnValueContains(themeColumn, theme));
      return this;
    }

    @Step("Отписываемся от группы тем {groupName}")
    public MySubscriptionsPage deleteThemeGroupSubscription (String groupName){
        clickCreateSubscription();
        clickGlobal();
        clickUnSubscribeThemeGroup(groupName);
        return this;
    }

    @Step("Проверяем что Группа тем исчезла из подписок")
    public MySubscriptionsPage checkNoThemeGroupSubscription(String context, String themeGroup){
        returnToMySubscriptionPage();
        if(new Table(contextColumn).isColumnValueEquals(contextColumn, globalContext)){
            clickContextButton(context);
            Assertions.assertFalse(new Table(tableHeader).isColumnValueContains(tableHeader, themeGroup));
        }
        return this;
    }

    @Step("Подписываемся на тему")
    public MySubscriptionsPage createThemeSubscription(String theme, String groupName){
        clickCreateSubscription();
        clickGlobal();
        DataTable themeGroupTable = new DataTable(tableHeader);
        themeGroupTable.searchAllPages(t -> themeGroupTable.isColumnValueEquals(tableHeader, groupName))
                .getRowByColumnValue(tableHeader, groupName)
                .getElementByColumn("").$x(".//button").click();
        new DataTable(themeColumn).getRowByColumnValue(themeColumn, theme)
                .getElementByColumn("Действие").$x("button").click();
        new Dialog("Создать подписку").setSelectValue("Приоритет", "Средний").clickButton("Создать");
            Alert.green("Подписка успешно создана");
            return this;
    }

    @Step("Редактируем подписку")
    public MySubscriptionsPage editSubscription(String theme){
        Menu.byElement($x("//td[.='{}']//..//button", theme)).select("Настроить подписку");
        new Dialog("Настроить подписку").setSelectValue("Приоритет", "Низкий").clickButton("Сохранить");
        Alert.green("Подписка успешно изменена");
        new Table("Группа тем").isColumnValueContains("Приоритет", "Низкий");


        return this;
    }

    @Step("Отписаться")
    public MySubscriptionsPage unSubscribe(String theme){
        Menu.byElement($x("//td[.='{}']//..//button", theme)).select("Отписаться");
        new Dialog("Подтверждение удаления подписки").clickButton("Да");
        Alert.green("Подписка удалена");

        return this;
    }




}
