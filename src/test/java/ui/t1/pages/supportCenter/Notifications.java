package ui.t1.pages.supportCenter;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import static com.codeborne.selenide.Selenide.$x;

public class Notifications {
    @Step("Проверяем название группы тем у сообщения")
    public void checkThemeGroup(String themeGroupName){
        Assertions.assertEquals(themeGroupName,
                $x("(//span[contains(@class, 'styles__GroupTheme')]) [2]").text());
    }

    @Step("Проверяем наличие метки 'непрочитано'")
    public void checkUnReadMark(){
    $x("(//span[contains(@class, 'styles__GroupTheme')]) [2]")
                .shouldHave(Condition.pseudo(":before",
                        "display",
                        "initial"));
    }

    @Step("Проверяем текст описания")
    public void checkDescription(String description){
        Assertions.assertEquals(description,
        $x("(//span[contains(@class, 'styles__Description')]) [1]").text());
    }

    @Step("Проверяем контекст")
    public void checkContext(String context){
        Assertions.assertEquals(context,
                $x("(//div[contains(@class, 'styles__Context')]) [1]").text());
    }

    @Step("Проверяем приоритет")
    public void checkImportance(String importance){
        Assertions.assertEquals(importance,
                $x("(//span[contains(@class, 'Importance')]) [1]").text());
    }
}
