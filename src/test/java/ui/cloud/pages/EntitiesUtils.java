package ui.cloud.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Table;
import ui.elements.TypifiedElement;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$x;

@Log4j2
public class EntitiesUtils {

    private static final ThreadLocal<Double> preBillingPrice = new ThreadLocal<>();

    public static double getPreBillingPrice() {
        return preBillingPrice.get();
    }

    public static void updatePreBillingPrice() {
        preBillingPrice.set(getPreBillingCostAction($x("//*[@data-testid='new-order-details-price' and contains(.,',')]").shouldBe(Condition.visible)));
    }

    @Step("Получение стоимости предбиллинга")
    public static double getPreBillingCostAction(SelenideElement element) {
        element.shouldBe(Condition.visible);
        double cost = Double.parseDouble(Objects.requireNonNull(StringUtils.findByRegex("([-]?\\d{1,5},\\d{2})", element.getText()))
                .replace(',', '.'));
        log.debug("Стоимость предбиллинга {}", cost);
        return cost;
    }

    @Step("Ожидание выполнение действия с продуктом")
    public static void waitChangeStatus(Table table, Duration duration) {
        table.getValueByColumnInFirstRow("Статус").scrollIntoView(TypifiedElement.scrollCenter).$$x("descendant::*[name()='svg']")
                .shouldBe(CollectionCondition.noneMatch("Ожидание заверешения действия", e ->
                        new ProductStatus(e).isNeedWaiting()), duration);
        Waiting.sleep(1000);
        List<String> titles = table.update().getValueByColumnInFirstRow("Статус").scrollIntoView(TypifiedElement.scrollCenter).$$x("descendant::*[name()='svg']")
                .shouldBe(CollectionCondition.sizeNotEqual(0))
                .shouldBe(CollectionCondition.allMatch("Ожидание отображение статусов", WebElement::isDisplayed))
                .stream().map(e -> new ProductStatus(e).getStatus()).collect(Collectors.toList());
        log.debug("Итоговый статус: {}", titles);
    }

    public static void clickOrder(){
        updatePreBillingPrice();
        Button.byText("Заказать").click();
        new Alert().checkColor(Alert.Color.GREEN).checkText("Заказ успешно создан").close();
    }

}
