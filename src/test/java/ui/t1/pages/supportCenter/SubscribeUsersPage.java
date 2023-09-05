package ui.t1.pages.supportCenter;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
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

    @Step("Подписываем пользователя на группу тем")
    public SubscribeUsersPage createAdminThemeGroupSubscription(String user) {
        $x("//input[@placeholder='Поиск по email, фамилии, имени']").click();
        $x("//input[@placeholder='Поиск по email, фамилии, имени']").setValue(user);
        $x("//div[contains(@class, 'dropdown-container')]//p[.='{}']", user).click();
        $x("//p[.='Подписки на темы в группе будут созданы с параметрами по умолчанию']").click();
        $x("//span[contains(@class, 'ChipValueStyled') and contains(.,'Tester Original')]").shouldBe(Condition.visible);
        $x("//span[.='Поле обязательно для заполнения']").shouldNotBe(Condition.visible);
        Waiting.sleep(300);
        $x("//button[.='Сохранить']").shouldBe(Condition.visible).click();
        alert.check(Alert.Color.GREEN, "Подписки успешно созданы");

        return this;
    }

    @Step("Возвращаемся на страницу Подписки пользователей организации")
    public SubscribeUsersPage backToAdminSubscriptionMain() {
        $x("//a[.='Подписки пользователей организации']").click();
        return this;
    }


    @Step("Проверяем со стороны администратора что пользователь подписан на группу тем")
    public SubscribeUsersPage checkAdminSubscriptions(String userEmail, String creatorEmail,  String... themes) {
        $x("//td[.='Глобальный']//..//button").shouldBe(Condition.visible).click();
        Table table = new Table(tableHeader);
        for (String theme : themes) {
            Assertions.assertTrue(table.isColumnValueEquals("Тема", theme));
            int subscribersCounter = Integer.parseInt($x("//td[.='{}']//../td [3]", theme).getText());
            $x("//td[.='{}']", theme).click();
            Waiting.sleep(300);
            Assertions.assertEquals(subscribersCounter, $$x("//tbody/tr[@role='row']").size() );
            $x("//td[.='{}']", userEmail).shouldBe(Condition.visible);
            $x("//td[.='{}']//..//td[.='{}']", userEmail, creatorEmail);
            backToThemes.click();
        }
        return this;
    }

    @Step("Отписываемся от группы тем")
    public SubscribeUsersPage unSubscribeThemeGroup(String context, String userEmail, String... themes) {
        Table table = new Table(tableHeader);
        for (String theme : themes) {
            Assertions.assertTrue(table.isColumnValueEquals("Тема", theme));
            $x("//td[.='{}']", theme).click();
            $x("//td[.='{}']//..//button", userEmail).click();
            $x("(//*[@role = 'menu']//*[text() = 'Удалить подписку'])[1]").click();
            new Dialog("Подтверждение удаления подписки").clickButton("Да");
            if($x("//table[thead/tr/th[.='Группа тем']]").is(Condition.visible)){
            Assertions.assertFalse(table.isColumnValueEquals("Получатель", userEmail));}
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
