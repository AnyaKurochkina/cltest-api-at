package ui.t1.pages.supportCenter;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.elements.*;

import static core.helper.StringUtils.$x;

public class MySubscriptionsPage {
    String tableHeader = "Группа тем";
    SelenideElement setPage = $x("//button[starts-with(@aria-label,'Страница')]");

    Alert alert = new Alert();

    @Step("Нажимаем Создать подписку")
    public MySubscriptionsPage clickCreateSubscription(){
        $x("//div[@data-testid='add-button']").click();
        return this;
    }

    @Step("Нажимаем Глобальные")
    public MySubscriptionsPage clickGlobal(){
        Button.byText("Глобальные").click();
        return this;
    }

    @Step("Переходим на страницу {page}")
    public MySubscriptionsPage setPage(int page){
        setPage.click();
        $x("//div[@role='listbox']").shouldBe(Condition.visible);
        $x("//div[@role='listbox']//div[contains(text(), '{}')]", page).click();
        return this;
    }

    public int getRowIndex(String groupName){
        int index = -1;
        int page = 0;
        while (index < 0){
        Table table = new Table(tableHeader);
        for (int i = 0; i < table.rowSize(); i++){
            if(table.getValueByColumnInRow(i, tableHeader).getText().equals(groupName)){
                index = i;
            }
        }
        if($x("//button[@aria-label='Следующая страница, выбрать']").isEnabled() && index == -1) {
            Button.byAriaLabel("Следующая страница, выбрать").click();
            page++;
         }
        else break;
        }
        if(index == -1){
            throw new RuntimeException("Группа тем не найдена");
        }
        setPage(page + 1);
        return (index + 2);
    }

    @Step("Нажимаем 'Подписаться' напротив Группы тем")
   public MySubscriptionsPage clickSubscribeThemeGroup(String groupName){
        while ($x("//td[.='{}']//..//button[.='Подписаться']").is(Condition.not(Condition.visible))){

        }

        $x("//td[.='{}']//..//button[.='Подписаться']", groupName).click();

        return this;
    }

    @Step("Нажимаем 'Отписаться' напротив Группы тем")
    public MySubscriptionsPage clickUnSubscribeThemeGroup(String groupName){
            $x("//td[.='{}']//..//button[.='Отписаться']", groupName).click();

            return this;
    }

    @Step("Подписываемся на группу тем {groupName}")
    public MySubscriptionsPage createThemeGroupSubscription(String groupName){
        clickCreateSubscription();
        clickGlobal();
        clickSubscribeThemeGroup(groupName);
        alert.check(Alert.Color.GREEN, "Подписки успешно созданы").waitClose();
        $x("//td[.='{}']//..//button[.='Отписаться']", groupName).shouldBe(Condition.visible);

        return this;
    }

    @Step("Проверяем что Группа тем появилась в подписках")
    public MySubscriptionsPage checkThemeGroupSubscription(String context, String ... themes){
        $x("//div[.='Мои подписки']").click();
        $x("//td[.='{}']//..//button", context).click();
        Table table = new Table(tableHeader);
        for(String theme : themes){
            Assertions.assertTrue(table.isColumnValueEquals("Тема", theme));
        }
        return this;
    }

    @Step("Раскрываем спойлер контекста")
    public MySubscriptionsPage clickContextButton(String context){
        $x("//td[.='{}']//..//button", context).click();
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
        $x("//td[.='{}']", theme).shouldBe(Condition.visible);

        return this;
    }

    @Step("Отписываемся от группы тем")
    public MySubscriptionsPage deleteThemeGroupSubscription (String groupName){
        clickCreateSubscription();
        clickGlobal();
        clickUnSubscribeThemeGroup(groupName);
        Dialog dialog = new Dialog("Подтверждение удаления подписок на группу тем");
        dialog.clickButton("Да");
        alert.check(Alert.Color.GREEN, "Подписки удалены").waitClose();
        $x("//td[.='{}']//..//button[.='Подписаться']", groupName).shouldBe(Condition.visible);

        return this;
    }

    @Step("Проверяем что Группа тем исчезла из подписок")
    public MySubscriptionsPage checkNoThemeGroupSubscription(String context){
        $x("//div[.='Мои подписки']").click();
        $x("//span[contains(text(), '{}')]//..//..//button", context).shouldNotBe(Condition.visible);
        return this;

    }

    @Step("Подписываемся на тему")
    public MySubscriptionsPage createThemeSubscription(String theme, String groupName, String context){
        clickCreateSubscription();
        clickGlobal();
        int rowIndex = getRowIndex(groupName);
        if (rowIndex > 0){
            $x("(//td[.='{}']//..//button)[2]", groupName).click();
            $x("//td[.='{}']//..//button", theme).click();
            new Dialog("Создать подписку").setSelectValue("Приоритет", "Средний").clickButton("Создать");
            alert.check(Alert.Color.GREEN, "Подписка успешно создана");
            alert.waitClose();
        }
        else {
            throw new NotFoundException("В таблице нет такой группы тем");
        }




        return this;
    }

    @Step("Редактируем подписку")
    public MySubscriptionsPage editSubscription(String theme){
        Menu.byElement($x("//td[.='{}']//..//button", theme)).select("Настроить подписку");
        new Dialog("Настроить подписку").setSelectValue("Приоритет", "Низкий").clickButton("Сохранить");
        alert.check(Alert.Color.GREEN, "Подписка успешно изменена");
        new Table("Группа тем").isColumnValueContains("Приоритет", "Низкий");
        alert.waitClose();

        return this;
    }

    @Step("Отписаться")
    public MySubscriptionsPage unSubscribe(String theme){
        Menu.byElement($x("//td[.='{}']//..//button", theme)).select("Отписаться");
        new Dialog("Подтверждение удаления подписки").clickButton("Да");
        alert.check(Alert.Color.GREEN, "Подписка удалена");
        alert.waitClose();

        return this;
    }




}
