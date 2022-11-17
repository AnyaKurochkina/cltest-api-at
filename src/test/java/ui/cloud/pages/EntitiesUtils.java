package ui.cloud.pages;

import com.codeborne.selenide.CollectionCondition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import ui.elements.Table;
import ui.elements.TypifiedElement;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class EntitiesUtils {

    @Step("Ожидание выполнение действия с продуктом")
    public static void waitChangeStatus(Table table, Duration duration) {
        table.getValueByColumnInFirstRow("Статус").scrollIntoView(TypifiedElement.scrollCenter).$$x("descendant::*[name()='svg']")
                .shouldBe(CollectionCondition.noneMatch("Ожидание заверешения действия", e ->
                        new ProductStatus(e).isNeedWaiting()), duration);
        Waiting.sleep(1000);
        List<String> titles = table.getValueByColumnInFirstRow("Статус").scrollIntoView(TypifiedElement.scrollCenter).$$x("descendant::*[name()='svg']")
                .shouldBe(CollectionCondition.sizeNotEqual(0))
                .shouldBe(CollectionCondition.allMatch("Ожидание отображение статусов", WebElement::isDisplayed))
                .stream().map(e -> new ProductStatus(e).getStatus()).collect(Collectors.toList());
        log.debug("Итоговый статус: {}", titles);
    }

}
