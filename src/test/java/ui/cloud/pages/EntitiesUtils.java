package ui.cloud.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import ui.elements.Table;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class EntitiesUtils {
    private final SelenideElement graphScheme = Selenide.$x("//canvas");
    private final SelenideElement closeModalWindowButton = Selenide.$x("//div[@role='dialog']//button[contains(.,'Закрыть')]");

    @Step("Ожидание выполнение действия с продуктом")
    public static void waitChangeStatus(Table table, Duration duration) {
        table.getValueByColumnInFirstRow("Статус").scrollIntoView(true).$$x("descendant::*[@title]")
                .shouldBe(CollectionCondition.noneMatch("Ожидание заверешения действия", e ->
                        ProductStatus.isNeedWaiting(e.getAttribute("title"))), duration);
        List<String> titles = table.getValueByColumnInFirstRow("Статус").scrollIntoView(true).$$x("descendant::*[@title]")
                .shouldBe(CollectionCondition.sizeNotEqual(0))
                .shouldBe(CollectionCondition.allMatch("Ожидание отображение статусов", WebElement::isDisplayed))
                .stream().map(e -> e.getAttribute("title")).collect(Collectors.toList());
        log.debug("Итоговый статус: {}", titles);
    }

    @Step("Проверка схемы выполнения графа")
    public void checkGraphScheme() {
        graphScheme.shouldBe(Condition.visible);
        //
        closeModalWindowButton.shouldBe(Condition.enabled).click();
    }
}
