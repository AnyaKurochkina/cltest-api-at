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

    @Step("Переходим на страницу {page}")
    public MySubscriptionsPage setPage(int page){
        setPage.click();
        $x("//div[@role='listbox']").shouldBe(Condition.visible);
        $x("//div[@role='listbox']//div[contains(text(), '{}')]", page).click();
        return this;
    }

    int getRowIndex(String groupName){
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

    }
        setPage(page + 1);
        return (index + 2);
    }

    @Step("Подписываемся на группу тем")
    public MySubscriptionsPage createThemeGroupSubscription(String groupName){

        int rowIndex;
        clickCreateSubscription();
        rowIndex = getRowIndex(groupName);
        if (rowIndex >= 0){
          $x("(//tr[@role='row'])[" + rowIndex + "]//button[.='Подписаться']").click();
        }
        else {
            throw new NotFoundException("В таблице нет такой группы тем");
        }
        alert.check(Alert.Color.GREEN, "Подписки успешно созданы");
        $x("(//tr[@role='row'])[" + rowIndex + "]//button[.='Отписаться']").shouldBe(Condition.visible);

        return this;
    }

    @Step("Проверяем что Группа тем появилась в подписках")
    public MySubscriptionsPage checkThemeGroupSubscription(String context, String ... themes){
        $x("//div[.='Мои подписки']").click();
        $x("//span[contains(text(), '{}')]//..//..//button", context).click();
        Table table = new Table(tableHeader);
        for(String theme : themes){
            Assertions.assertTrue(table.isColumnValueEquals("Тема", theme));
        }
        return this;
    }

    @Step("Проверяем что тема появилась в подписках")
    public MySubscriptionsPage checkThemeSubscription(String theme, String context){
        $x("//div[.='Мои подписки']").click();
        $x("(//td[.='{}']//..//button)", context).click();
        $x("//td[.='{}']", theme).shouldBe(Condition.visible);

        return this;
    }

    @Step("Проверяем что темы нет в подписках")
    public MySubscriptionsPage checkNoThemeSubscription(String theme, String groupName){
        $x("//div[.='Мои подписки']").click();
        $x("(//td[.='{}']//..//button)", groupName).click();
        $x("//td[.='{}']", theme).shouldNotBe(Condition.visible);

        return this;
    }

    @Step("Отписываемся от группы тем")
    public MySubscriptionsPage deleteThemeGroupSubscription(String groupName){

        int rowIndex;
        clickCreateSubscription();
        rowIndex = getRowIndex(groupName);
        if (rowIndex > 0){
            $x("(//tr[@role='row'])[" + rowIndex + "]//button[.='Отписаться']").click();
        }
        else {
            throw new NotFoundException("В таблице нет такой группы тем");
        }
        Dialog dialog = new Dialog("Подтверждение удаления подписок на группу тем");
        dialog.clickButton("Да");
        alert.check(Alert.Color.GREEN, "Подписки удалены");
        $x("(//tr[@role='row'])[" + rowIndex + "]//button[.='Подписаться']").shouldBe(Condition.visible);

        return this;
    }

    @Step("Проверяем что Группа тем исчезла из подписок")
    public MySubscriptionsPage checkNoThemeGroupSubscription(String context){
        $x("//div[.='Мои подписки']").click();
        $x("//span[contains(text(), '{}')]//..//..//button", context).shouldNotBe(Condition.visible);
        return this;

    }

    @Step("Меняем контекст")
    public String setContext(int index){
        $x("(//div[starts-with(@class,'UserContext')])[1]").click();
      String context =  $x("(//div[@class='title-wrapper']//p)[" + index + "]").getText();
        $x("(//div[@class='title-wrapper'])[" + index + "]").click();

        return context;
    }

    @Step("Подписываемся на тему")
    public MySubscriptionsPage createThemeSubscription(String theme, String groupName, String context){
        clickCreateSubscription();
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
