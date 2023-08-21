package ui.t1.pages.supportCenter;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.Table;

import static core.helper.StringUtils.$$x;
import static core.helper.StringUtils.$x;

public class SubscribeUsersPage extends MySubscriptionsPage {

    Button backToThemes = Button.byText("Вернуться к темам");

    @Step("Подписываемся на группу тем")
    public SubscribeUsersPage createAdminThemeGroupSubscription(String user) {
       Dialog dialog =  Dialog.byTitle("Создать подписки");
        $x("//h2[.='Создать подписки']/ancestor::div[@role='dialog']" +
                "/descendant::div[label[starts-with(.,'Получатели')]]//input").setValue(user);
        $x("//option[.='{}']", user).click();
        dialog.setSelectValue("Получатели", user);
        dialog.clickButton("Сохранить");

        alert.check(Alert.Color.GREEN, "Подписка успешно создана");

        return this;
    }

    @Step("Возвращаемся на страницу Подписки пользователей организации")
    public SubscribeUsersPage backToAdminSubscriptionMain() {
        $x("//a[.='Подписки пользователей организации']").click();
        return this;
    }


    @Step("Проверяем со стороны администратора что пользователь подписан на группу тем")
    public SubscribeUsersPage checkAdminSubscriptions(String userEmail, String creatorEmail, String context, String... themes) {
        clickContextButton(context);
        Table table = new Table(tableHeader);
        for (String theme : themes) {
            Assertions.assertTrue(table.isColumnValueEquals("Тема", theme));
            int subscribersCounter = Integer.parseInt($x("//td[.='{}']//../td [3]", theme).getText());
            $x("//td[.='{}']", theme).click();
            Assertions.assertEquals(subscribersCounter, $$x("//tbody/tr[@role='row']").size());
            $x("//td[.='{}']", userEmail).shouldBe(Condition.visible);
            $x("//td[.='{}']//..//td[.='{}']", userEmail, creatorEmail);
            backToThemes.click();
        }
        return this;
    }

    @Step("Отписываемся от группы тем")
    public SubscribeUsersPage unSubscribeThemeGroup(String context, String userEmail, String... themes) {
        clickContextButton(context);
        Table table = new Table(tableHeader);
        for (String theme : themes) {
            Assertions.assertTrue(table.isColumnValueEquals("Тема", theme));
            $x("//td[.='{}']", theme).click();
            $x("//td[.='{}']//..//button", userEmail).click();
            $x("(//*[@role = 'menu']//*[text() = 'Удалить подписку'])[1]").click();
            new Dialog("Подтверждение удаления подписки").clickButton("Да");
            Assertions.assertFalse(table.isColumnValueEquals("Получатель", userEmail));
            backToThemes.click();
        }
        return this;

    }

    @Step("Нажимаем 'Создать подписки' напротив Группы тем")
    public MySubscriptionsPage clickSubscribeThemeGroup(int rowIndex){
        if (rowIndex >= 0){
            $x("(//tr[@role='row'])[" + rowIndex + "]//button[.='Создать подписки']").click();
        }
        else {
            throw new NotFoundException("В таблице нет такой группы тем");
        }
        return this;
    }

}
