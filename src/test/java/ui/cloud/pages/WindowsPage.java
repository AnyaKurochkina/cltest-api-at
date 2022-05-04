package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.function.Executable;

public class WindowsPage extends IProductPage {

    public WindowsPage() {
        btnGeneralInfo.shouldBe(Condition.enabled);
    }

    public void delete() {
        runActionWithParameters("Виртуальная машина", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            dlgActions.getDialog().$x("descendant::button[.='Удалить']")
                    .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        });
    }

    public void start() {
        runActionWithoutParameters("Виртуальная машина", "Включить");
    }

    public void restart() {
        runActionWithoutParameters("Виртуальная машина", "Перезагрузить по питанию");
    }
    public void stopHard() {
        runActionWithoutParameters("Виртуальная машина", "Выключить принудительно");
    }

}
