package ui.t1.pages.supportCenter;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

import static core.helper.StringUtils.$x;

public class SubscribeUsersPage extends MySubscriptionsPage {

    Button backToThemes = Button.byText("Вернуться к темам");
    String contextColumn = "Контекст";
    String global = "Глобальный";
    String themeColumn = "Тема";
    String creatorColumn = "Создатель";
    String recipientsColumn = "Получатели";
    String recipientColumn = "Получатель";

    @Step("Подписываем пользователя на группу тем")
    public SubscribeUsersPage createAdminThemeGroupSubscription(String user) {
        Input.byPlaceholder("Поиск по email, фамилии, имени").click();
        Input.byPlaceholder("Поиск по email, фамилии, имени").setValue(user);
        $x("//div[contains(@class, 'dropdown-container')]//p[.='{}']", user).click();
        $x("//p[.='Подписки на темы в группе будут созданы с параметрами по умолчанию']").click();
        $x("//span[contains(@class, 'ChipValueStyled') and contains(.,'Tester Original')]").shouldBe(Condition.visible);
        $x("//span[.='Поле обязательно для заполнения']").shouldNotBe(Condition.visible);
        Waiting.sleep(300);
        $x("//button[.='Сохранить']").shouldBe(Condition.visible).click();
        Alert.green("Подписки успешно созданы");

        return this;
    }

    @Step("Возвращаемся на страницу Подписки пользователей организации")
    public SubscribeUsersPage backToAdminSubscriptionMain() {
      Button.byXpath("//a[.='Подписки пользователей организации']").click();
        return this;
    }


    @Step("Проверяем со стороны администратора что пользователь подписан на группу тем")
    public SubscribeUsersPage checkAdminSubscriptions(String userEmail, String creatorEmail,  String... themes) {
        DataTable themeGroupsTable = new DataTable(contextColumn);
        themeGroupsTable.searchAllPages(t -> themeGroupsTable.isColumnValueEquals(contextColumn, global))
                        .getRowByColumnValue(contextColumn, global).getElementLastColumn().$x("..//button").click();
        for (String theme : themes) {
            DataTable themesTable = new DataTable(tableHeader);
            Table.Row themeRow = themesTable.getRowByColumnValue(themeColumn, theme);
            int subscribersCounter = Integer.parseInt(themeRow.getValueByColumn(recipientsColumn));
            themeRow.getElementByColumn(themeColumn).click();
            DataTable recipientsTable = new DataTable(recipientColumn);
            Assertions.assertEquals(subscribersCounter, recipientsTable.rowSize());
            Assertions.assertTrue(recipientsTable.isColumnValueEquals(recipientColumn, userEmail));
            Assertions.assertTrue(recipientsTable.isColumnValueEquals(creatorColumn,creatorEmail ));
            backToThemes.click();
        }
        return this;
    }

    @Step("Отписываемся от группы тем")
    public SubscribeUsersPage unSubscribeThemeGroup(String userEmail, String... themes) {
        for (String theme : themes) {
            Table themesTable = new Table(themeColumn);
            themesTable.getRowByColumnValue(themeColumn, theme).getElementByColumn(themeColumn).click();
            DataTable recipientsTable = new DataTable(recipientColumn);
            Menu.byElement(recipientsTable
                            .getRowByColumnValue(recipientColumn, userEmail)
                            .getElementLastColumn()
                            .$x("..//button"))
                            .select("Удалить подписку");
            new Dialog("Подтверждение удаления подписки").clickButton("Да");
            Alert.green("Подписки удалены");
            backToThemes.click();
            }

        return this;
    }

    @Step("Нажимаем 'Создать подписки' напротив Группы тем")
    public MySubscriptionsPage clickSubscribeThemeGroup(String groupName){
        DataTable table =  new DataTable(tableHeader);
        table.searchAllPages(t -> table.isColumnValueEquals(tableHeader, groupName))
                .getRowByColumnValue(tableHeader, groupName)
                .getElementByColumn("Действие").$("button").click();

        return this;
    }
}
