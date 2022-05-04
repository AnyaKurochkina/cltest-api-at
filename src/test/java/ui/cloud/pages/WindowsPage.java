package ui.cloud.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$x;

public class WindowsPage {
    SelenideElement btnGeneralInfo = $x("//button[.='Общая информация']");
    SelenideElement btnHistory = $x("//button[.='История действий']");
    SelenideElement btnActions = $x("//button[.='Действия']");
    SelenideElement btnDelete = $x("//li[.='Удалить']");
    TopInfo topInfo = new TopInfo();
    Condition activeCnd = Condition.and("visible and enabled", Condition.visible, Condition.enabled);
    Condition clickableCnd = Condition.not(Condition.cssValue("cursor", "default"));

    public WindowsPage() {
        btnGeneralInfo.shouldBe(Condition.enabled);
    }

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

    public void delete() {
        btnGeneralInfo.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        btnActions.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        btnDelete.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Dialog dlgActions = new Dialog("Удаление");
        dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        dlgActions.getDialog().$x("descendant::button[.='Удалить']")
                .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
    }

    private static class History extends TablePage {
        History() {
            super("Дата запуска");
        }
        public String lastActionStatus() {
            return getFirstRowByColumn("Статус").$x("descendant::*[@title]").getAttribute("title");
        }
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
}
