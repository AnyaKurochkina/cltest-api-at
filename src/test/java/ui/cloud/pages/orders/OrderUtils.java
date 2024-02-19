package ui.cloud.pages.orders;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.function.Executable;
import org.openqa.selenium.WebElement;
import steps.stateService.StateServiceSteps;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Table;
import ui.elements.TypifiedElement;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$x;

@Log4j2
public class OrderUtils {

    private static final ThreadLocal<Double> preBillingPrice = new ThreadLocal<>();

    public static Double getPreBillingPrice() {
        return preBillingPrice.get();
    }

    public static void setPreBillingPrice(Double price) {
        preBillingPrice.set(price);
    }

    public static void updatePreBillingPrice() {
        if (NewOrderPage.getCalculationDetailsHeader().exists()) {
            Waiting.sleep(2000);
            preBillingPrice.set(getCostValue($x("//*[@data-testid='new-order-details-price' and contains(.,',')]")
                    .shouldBe(Condition.visible.because("Не отобразилась стоимость предбиллинга"))));
        } else preBillingPrice.set(null);
    }

    public static String getCurrentProjectId() {
        return StringUtils.findByRegex("context=([^&]*)", WebDriverRunner.getWebDriver().getCurrentUrl());
    }

    @SneakyThrows
    public static void waitCreate(Executable executable) {
        try {
            executable.execute();
        } catch (Throwable e) {
            Exception exception = new Exception("Последняя ошибка:\n" + StateServiceSteps.getLastErrorByProjectId(OrderUtils.getCurrentProjectId()));
            e.addSuppressed(exception);
            throw e;
        }
    }

    public static Double getCostValue(SelenideElement element) {
        element.shouldBe(Condition.visible.because("Должно отображаться сообщение"));
        final String text = TypifiedElement.findNotEmptyText(element, Duration.ofSeconds(5));
        log.debug("Стоимость '{}'", text);
        String cost = StringUtils.findByRegex("(-?[\\d\\s]+,\\d{2})", text);
        if (cost == null)
            return null;
        return Double.parseDouble(cost.replace(',', '.').replaceAll(" ", ""));
    }

    @Step("Ожидание выполнения действия с продуктом")
    public static void waitChangeStatus(Table table, Duration duration) {
        table.getValueByColumnInFirstRow("Статус").scrollIntoView(TypifiedElement.scrollCenter).$$x("descendant::*[name()='svg']")
                .shouldBe(CollectionCondition.sizeNotEqual(0))
                .shouldBe(CollectionCondition.allMatch("Ожидание отображение статусов", WebElement::isDisplayed))
                .shouldBe(CollectionCondition.noneMatch("Ожидание завершения действия", e ->
                        new OrderStatus(e).isNeedWaiting()), duration);
        Waiting.sleep(1000);
        List<String> titles = table.update().getValueByColumnInFirstRow("Статус").scrollIntoView(TypifiedElement.scrollCenter).$$x("descendant::*[name()='svg']")
                .shouldBe(CollectionCondition.sizeNotEqual(0))
                .shouldBe(CollectionCondition.allMatch("Ожидание отображение статусов", WebElement::isDisplayed))
                .stream().map(e -> new OrderStatus(e).getStatus()).collect(Collectors.toList());
        log.debug("Итоговый статус: {}", titles);
    }

    public static void waitStatus(Table table, String status, Duration duration) {
        table.getValueByColumnInFirstRow("Статус").scrollIntoView(TypifiedElement.scrollCenter).$$x("descendant::*[name()='svg']")
                .shouldBe(CollectionCondition.anyMatch("Ожидание завершения действия", e ->
                        new OrderStatus(e).getStatus().equals(status)), duration);
    }

    public static void clickOrder() {
        updatePreBillingPrice();
        Button.byText("Заказать").click();
        Alert.green("Заказ успешно создан");
    }

    public static void clickOrder(String errorMessage) {
        updatePreBillingPrice();
        Button.byText("Заказать").click();
        Alert.red(errorMessage);
    }

    public static void checkOrderCost(double prebillingCost, IProductPage orderPage) {
        Waiting.find(() -> Math.abs(prebillingCost - orderPage.getOrderCost()) <= 0.01, Duration.ofSeconds(60),
                StringUtils.format("Стоимость заказа '{}' отличается от предбиллинга '{}'", orderPage.getOrderCost(), prebillingCost));
    }
}
