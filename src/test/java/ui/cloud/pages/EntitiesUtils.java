package ui.cloud.pages;

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
import java.util.Objects;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$x;

@Log4j2
public class EntitiesUtils {

    private static final ThreadLocal<Double> preBillingPrice = new ThreadLocal<>();

    public static Double getPreBillingPrice() {
        return preBillingPrice.get();
    }

    public static void setPreBillingPrice(Double price) {
        preBillingPrice.set(price);
    }

    public static void updatePreBillingPrice() {
        if(Product.getCalculationDetails().exists()) {
            preBillingPrice.set(getPreBillingCostAction($x("//*[@data-testid='new-order-details-price' and contains(.,',')]").shouldBe(Condition.visible)));
        }
        else preBillingPrice.set(null);
    }

    public static String getCurrentProjectId(){
        return StringUtils.findByRegex("context=([^&]*)", WebDriverRunner.getWebDriver().getCurrentUrl());
    }

    @SneakyThrows
    public static void waitCreate(Executable executable){
        try {
            executable.execute();
        } catch (Throwable e) {
            Exception exception = new Exception("Последняя ошибка:\n" + StateServiceSteps.getLastErrorByProjectId(EntitiesUtils.getCurrentProjectId()));
            e.addSuppressed(exception);
            throw e;
        }
    }

    public static double getPreBillingCostAction(SelenideElement element) {
        return Double.parseDouble(Objects.requireNonNull(StringUtils.findByRegex("([-]?[\\d\\s]{1,},\\d{2})", element.getText()))
                .replace(',', '.').replaceAll(" ", ""));
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

    public static void waitStatus(Table table, String status, Duration duration) {
        table.getValueByColumnInFirstRow("Статус").scrollIntoView(TypifiedElement.scrollCenter).$$x("descendant::*[name()='svg']")
                .shouldBe(CollectionCondition.anyMatch("Ожидание заверешения действия", e ->
                        new ProductStatus(e).getStatus().equals(status)), duration);
    }

    public static void clickOrder(){
        updatePreBillingPrice();
        Button.byText("Заказать").click();
        Alert.green("Заказ успешно создан");
    }
}
