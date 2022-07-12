package ui.cloud.pages;

import com.codeborne.selenide.*;
import core.exception.CreateEntityException;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import models.orderService.interfaces.IProduct;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import steps.stateService.StateServiceSteps;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Input;
import ui.elements.Table;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.open;
import static core.helper.StringUtils.$x;
import static tests.Tests.activeCnd;
import static tests.Tests.clickableCnd;

@Log4j2
@Getter
public abstract class IProductPage {
    IProduct product;
    double preBillingCostAction;

    SelenideElement btnHistory = $x("//button[.='История действий']");
    SelenideElement btnGeneralInfo = $x("//button[.='Общая информация']");
    private final SelenideElement currentPriceOrder = Selenide.$x("(//p[contains(.,'₽/сут.') and contains(.,',')])[1]");
    private final SelenideElement preBillingPriceAction = Selenide.$x("(//p[contains(.,'₽/сут.') and contains(.,',')])[2]");
    private final SelenideElement closeModalWindowButton = Selenide.$x("//div[@role='dialog']//button[contains(.,'Закрыть')]");
    private final SelenideElement graphScheme = Selenide.$x("//canvas");

    public IProductPage(IProduct product) {
        if (Objects.nonNull(product.getError()))
            throw new CreateEntityException(String.format("Продукт необходимый для выполнения теста был создан с ошибкой:\n%s", product.getError()));
        if (Objects.nonNull(product.getLink()))
            open(product.getLink());
        btnGeneralInfo.shouldBe(Condition.enabled);
        product.setLink(WebDriverRunner.getWebDriver().getCurrentUrl());
        product.addLinkProduct();
        this.product = product.buildFromLink();
    }

    public void waitChangeStatus() {
        waitChangeStatus(Duration.ofMinutes(8));
    }

    @Step("Ожидание выполнение действия с продуктом")
    public void waitChangeStatus(Duration duration) {
        new TopInfo().getValueByColumnInFirstRow("Статус").scrollIntoView(true).$$x("descendant::*[@title]")
                .shouldBe(CollectionCondition.noneMatch("Ожидание заверешения действия", e ->
                        ProductStatus.isNeedWaiting(e.getAttribute("title"))), duration);
        List<String> titles = new TopInfo().getValueByColumnInFirstRow("Статус").scrollIntoView(true).$$x("descendant::*[@title]")
                .shouldBe(CollectionCondition.sizeNotEqual(0))
                .shouldBe(CollectionCondition.allMatch("Ожидание отображение статусов", WebElement::isDisplayed))
                .stream().map(e -> e.getAttribute("title")).collect(Collectors.toList());
        log.debug("Итоговый статус: {}", titles);
    }

    @Step("Переключение 'Защита от удаления' в состояние '{expectValue}'")
    public void switchProtectOrder(String expectValue) {
        runActionWithParameters(getLabel(), "Защита от удаления", "Подтвердить", () -> {
            Input.byLabel("Включить защиту от удаления").click();
        }, ActionParameters.builder().waitChangeStatus(false).checkPreBilling(false).checkLastAction(false).build());
        new TopInfo().getValueByColumnInFirstRow("Защита от удаления").$("*").shouldBe(Condition.attribute("title", expectValue));
    }

    public SelenideElement getBtnAction(String header) {
        return $x("//ancestor::*[.='{}']/parent::*//button[@id='actions-menu-button']", header);
    }

    @Step("Получение label")
    public String getLabel() {
        return $x("//span[starts-with(text(),'AT-UI-')]").shouldBe(Condition.visible).getText();
    }

    @Step("Запуск действия '{action}'")
    protected void runActionWithoutParameters(SelenideElement button, String action, ActionParameters params) {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        button.shouldBe(activeCnd).scrollIntoView("{block: 'center'}").hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Dialog dlgActions = new Dialog(action);
        if (params.isCheckPreBilling())
            preBillingCostAction = getPreBillingCostAction(preBillingPriceAction);
        dlgActions.getDialog().$x("descendant::button[.='Подтвердить']")
                .shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (params.isCheckAlert())
            new Alert().checkText(action).checkColor(Alert.Color.GREEN).close();
        if (params.isWaitChangeStatus())
            waitChangeStatus();
        if (params.isCheckLastAction())
            checkLastAction(action);
        btnGeneralInfo.shouldBe(Condition.enabled).click();
    }

    @SneakyThrows
    @Step("Запуск действия '{action}' с параметрами и последующим нажатием на кнопку {textButton}")
    protected void runActionWithParameters(SelenideElement button, String action, String textButton, Executable executable, ActionParameters params) {
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        button.shouldBe(activeCnd).scrollIntoView("{block: 'center'}").hover().shouldBe(clickableCnd).click();
        $x("//li[.='{}']", action).shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        executable.execute();
        if (params.isCheckPreBilling())
            preBillingCostAction = getPreBillingCostAction(preBillingPriceAction);
        SelenideElement runButton = $x("//div[@role='dialog']//button[.='{}']", textButton);
        runButton.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        if (params.isCheckAlert())
            new Alert().checkText(action).checkColor(Alert.Color.GREEN).close();
        if (params.isWaitChangeStatus())
            waitChangeStatus();
        if (params.isCheckLastAction())
            checkLastAction(action);
    }

    protected void runActionWithParameters(SelenideElement button, String action, String textButton, Executable executable) {
        runActionWithParameters(button, action, textButton, executable, ActionParameters.builder().build());
    }

    protected void runActionWithoutParameters(SelenideElement button, String action) {
        runActionWithoutParameters(button, action, ActionParameters.builder().build());
    }

    protected void runActionWithoutParameters(String headerBlock, String action, ActionParameters params) {
        runActionWithoutParameters(getBtnAction(headerBlock), action, params);
    }

    protected void runActionWithoutParameters(String headerBlock, String action) {
        runActionWithoutParameters(getBtnAction(headerBlock), action, ActionParameters.builder().build());
    }

    public void runActionWithParameters(String headerBlock, String action, String textButton, Executable executable, ActionParameters params) {
        runActionWithParameters(getBtnAction(headerBlock), action, textButton, executable, params);
    }

    public void runActionWithParameters(String headerBlock, String action, String textButton, Executable executable) {
        runActionWithParameters(getBtnAction(headerBlock), action, textButton, executable, ActionParameters.builder().build());
    }

    @Step("Проверка статуса заказа")
    public void checkErrorByStatus(String status) {
        if (status.equals(ProductStatus.ERROR)) {
            Assertions.fail(String.format("Ошибка выполнения action продукта: %s. \nИтоговый статус: %s . \nОшибка: %s",
                    product, status, StateServiceSteps.GetErrorFromStateService(product.getOrderId())));
        }
    }

    @Step("Проверка на содержание неоюходимых столбцов на вкладке История действий")
    public void checkHeadersHistory() {
        Assertions.assertEquals(Arrays.asList("Наименование", "Инициатор", "Дата создания", "Дата запуска", "Продолжительность, сек",
                "Статус", "Просмотр"), new History().getHeaders());
    }

    private static class TopInfo extends Table {
        public TopInfo() {
            super("Защита от удаления");
        }
    }

    public class History extends Table {
        public History() {
            super("Дата запуска");
        }

        @Override
        protected void open() {
            btnHistory.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        }

        public String lastActionName() {
            return getValueByColumnInFirstRow("Наименование").getText();
        }

        public String lastActionStatus() {
            return getValueByColumnInFirstRow("Статус").$$x("descendant::*[@title]")
                    .shouldBe(CollectionCondition.allMatch("Ожидание отображение статусов", WebElement::isDisplayed))
                    .stream()
                    .map(e -> e.getAttribute("title"))
                    .filter(Objects::nonNull)
                    .filter(ProductStatus::isStatus)
                    .findFirst()
                    .orElseThrow(NotFoundException::new);
        }
    }

    @SneakyThrows
    @Step("Запуска действия с проверкой стоимости")
    public void runActionWithCheckCost(CompareType type, Executable executable) {
        Selenide.refresh();
        waitChangeStatus();
        double currentCost = getCostOrder();
        executable.execute();
        Selenide.refresh();
        currentPriceOrder.shouldBe(Condition.matchText(String.valueOf(preBillingCostAction).replace('.', ',')), Duration.ofMinutes(3));
        Assertions.assertEquals(preBillingCostAction, getCostOrder(), "Стоимость предбиллинга экшена не равна стоимости после выполнения действия");
        if (type == CompareType.MORE)
            Assertions.assertTrue(preBillingCostAction > currentCost, String.format("%f <= %f", preBillingCostAction, currentCost));
        else if (type == CompareType.LESS)
            Assertions.assertTrue(preBillingCostAction < currentCost, String.format("%f >= %f", preBillingCostAction, currentCost));
        else if (type == CompareType.EQUALS)
            Assertions.assertEquals(preBillingCostAction, currentCost, 0.01d);
        else if (type == CompareType.ZERO) {
            Assertions.assertEquals(0.0d, preBillingCostAction, 0.001d);
            Assertions.assertEquals(0.0d, currentCost, 0.001d);
        }
    }

    protected abstract class VirtualMachine extends Table {
        public static final String POWER_STATUS_DELETED = "Удалено";
        public static final String POWER_STATUS_ON = "Включено";
        public static final String POWER_STATUS_OFF = "Выключено";

        abstract String getPowerStatus();

        @Override
        protected void open() {
            btnGeneralInfo.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        }

        public VirtualMachine(String columnName) {
            super(columnName);
        }

        public String getPowerStatus(String header) {
            return getValueByColumnInFirstRow(header).$x("descendant::*[@title]").getAttribute("title");
        }

        public void checkPowerStatus(String status) {
            Assertions.assertEquals(status, getPowerStatus(), "Статус питания не соотвествует ожидаемому");
        }
    }

    @Step("Проверка выполнения действия {action}")
    public void checkLastAction(String action) {
        btnHistory.shouldBe(Condition.enabled).click();
        History history = new History();
        checkErrorByStatus(history.lastActionStatus());
        Assertions.assertEquals(history.lastActionName(), action, "Название последнего действия не соответствует ожидаемому");
    }

    public History getHistoryTable() {
        return new History();
    }

    @Step("Получение стоимости заказа")
    public double getCostOrder() {
        currentPriceOrder.shouldBe(Condition.visible, Duration.ofMinutes(3));
        double cost = Double.parseDouble(Objects.requireNonNull(StringUtils.findByRegex("([-]?\\d{1,5},\\d{2})", currentPriceOrder.getText()))
                .replace(',', '.'));
        log.debug("Стоимость заказа {}", cost);
        return cost;
    }

    @Step("Получение стоимости предбиллинга")
    public static double getPreBillingCostAction(SelenideElement element) {
        element.shouldBe(Condition.visible);
        double cost = Double.parseDouble(Objects.requireNonNull(StringUtils.findByRegex("([-]?\\d{1,5},\\d{2})", element.getText()))
                .replace(',', '.'));
        log.debug("Стоимость предбиллинга {}", cost);
        return cost;
    }
}
