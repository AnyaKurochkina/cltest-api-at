package ui.cloud.pages;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.util.Objects;

import static core.helper.StringUtils.$x;

public abstract class IProductPage {
    TopInfo topInfo = new TopInfo();
    Condition activeCnd = Condition.and("visible and enabled", Condition.visible, Condition.enabled);
    Condition clickableCnd = Condition.not(Condition.cssValue("cursor", "default"));

    SelenideElement btnHistory = $x("//button[.='История действий']");
    SelenideElement btnGeneralInfo = $x("//button[.='Общая информация']");

    public void waitPending() {
        topInfo.getFirstRowByColumn("Статус").$$x("descendant::*[@title]")
                .shouldBe(CollectionCondition.noneMatch("Ожидание заверешения действия", e -> Objects.equals(e.getAttribute("title"), "Разворачивается") ||
                        Objects.equals(e.getAttribute("title"), "Изменение")), Duration.ofMillis(20000 * 1000));
    }

    public void checkLastAction() {
        btnHistory.shouldBe(Condition.enabled).click();
        History history = new History();
        Assertions.assertEquals("В порядке", history.lastActionStatus());
    }

    private SelenideElement getBtnAction(String header){
        return $x("//ancestor::div[.='{}Действия']//button[.='Действия']", header);
    }

    public void runActionWithoutParameters(String headerBlock, String action){
        getBtnAction(headerBlock).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click(ClickOptions.usingJavaScript());
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Dialog dlgActions = new Dialog(action);
        dlgActions.getDialog().$x("descendant::button[.='Подтвердить']")
                .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }

    @SneakyThrows
    public void runActionWithParameters(String headerBlock, String action, Executable executable){
        getBtnAction(headerBlock).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click(ClickOptions.usingJavaScript());
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        executable.execute();
    }

    private static class TopInfo extends TablePage {
        public TopInfo() {
            super("Защита от удаления");
        }

        public boolean isPending() {
            return getFirstRowByColumn("Статус").$$x("descendant::*[@title]").stream()
                    .anyMatch(e -> Objects.equals(e.getAttribute("title"), "Разворачивается") ||
                            Objects.equals(e.getAttribute("title"), "Изменение"));
        }
    }

    private static class History extends TablePage {
        History() {
            super("Дата запуска");
        }

        public String lastActionStatus() {
            return getFirstRowByColumn("Статус").$x("descendant::*[@title]").getAttribute("title");
        }
    }
}
